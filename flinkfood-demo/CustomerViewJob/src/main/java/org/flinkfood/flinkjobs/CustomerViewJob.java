// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.api.JsonOnNull;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.jsonObject;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.flinkfood.schemas.customer.Customer;
import org.flinkfood.schemas.customer.KafkaCustomerSchema;
import org.flinkfood.schemas.order.KafkaOrderSchema;
import org.flinkfood.schemas.order.Order;

// Class declaration for the Flink job
public class CustomerViewJob {
        // Kafka and MongoDB connection details obtained from environment variables
        // private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String KAFKA_URI = "localhost:9092";
        private static final String SOURCE_CUSTOMER_TABLE = "postgres.public.customer";
        private static final String SOURCE_ADDRESS_TABLE = "postgres.public.address";
        private static final String SOURCE_ORDER_TABLE = "postgres.public.order";
        // private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
        private static final String MONGODB_URI = "mongodb://localhost:27017";
        private static final String SINK_DB = "flinkfood";
        private static final String SINK_DB_TABLE = "users_sink";

        // Main method where the Flink job is defined
        public static void main(String[] args) throws Exception {

                // Setting up Kafka source with relevant configurations
                KafkaSource<Customer> sourceCustomer = KafkaSource.<Customer>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_CUSTOMER_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaCustomerSchema())
                                .build();

                // Setting up Kafka source with relevant configurations
                KafkaSource<Order> sourceOrder = KafkaSource.<Order>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_ORDER_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaOrderSchema())
                                .build();

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
                TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "500 ms");
                tableConfig.set("table.exec.mini-batch.size", "1000");

                DataStream<Customer> streamCustomer = env
                                .fromSource(sourceCustomer, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Customer::getId);

                Table customerTable = tableEnv.fromDataStream(streamCustomer);

                tableEnv.createTemporaryView("Customer", tableEnv.toChangelogStream(customerTable));

                DataStream<Order> streamOrder = env
                                .fromSource(sourceOrder, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Order::getCustomer_id);

                Table orderTable = tableEnv.fromDataStream(streamOrder);

                tableEnv.createTemporaryView("Orders", tableEnv.toChangelogStream(orderTable));

                tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");

                tableEnv.executeSql("CREATE TABLE CustomeView (\r\n" + //
                                "  id INT,\r\n" + //
                                "  first_name STRING,\r\n" + //
                                "  last_name STRING,\r\n" + //
                                "  orders ARRAY<row<id INT, name STRING, description STRING>>,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "   'connector' = 'mongodb',\r\n" + //
                                "   'uri' = 'mongodb://localhost:27017',\r\n" + //
                                "   'database' = 'flinkfood',\r\n" + //
                                "   'collection' = 'users_sink'\r\n" + //
                                ");");
                tableEnv.executeSql(
                                "INSERT INTO CustomeView SELECT DISTINCT  c.id,c.first_name,c.last_name,(SELECT ARRAY_AGGR(ROW(o.id,o.name,o.description)) FROM Orders o WHERE o.customer_id = c.id) FROM Customer c;");

                Table resultTable4 = tableEnv.sqlQuery(
                                "SELECT DISTINCT c.id,c.first_name,c.last_name,(SELECT ARRAY_AGGR(ROW(o.id,o.name,o.description)) FROM Orders o WHERE o.customer_id = c.id) FROM Customer c;");

                tableEnv.toChangelogStream(resultTable4).print();

                env.execute("CustomerViewJob");
        }
}

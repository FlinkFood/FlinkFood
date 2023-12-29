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

/**
 * This class, {@code CustomerViewJob}, represents a Flink job that creates the Customer View.
 * The job retrieves data from Kafka topics related to customers and orders and aggregates this data
 * to create a view of customers with their associated orders. The aggregated view is then stored in a MongoDB collection.
 * 
 * <p>
 * FOR A MORE DETAILED EXPLANATION OF THE CODE, PLEASE REFER TO THE README FILE "customerView.md"
 * </p>
 * 
 * <p>
 * The following steps outline the functionality of this job:
 * 1. Define necessary Kafka and MongoDB connection details.
 * 2. Set up the Flink execution environment and stream table environment.
 * 3. Register user-defined functions for aggregation.
 * 4. Define tables for customers and orders sourced from Kafka topics.
 * 5. Create a view table that represents an aggregated view of customers with their orders.
 * 6. Execute the Flink job.
 * </p>
 * 
 * <p>
 * MAKE SURE THE CORRECT KAFKA AND MONGODB CONNECTION DETAILS ARE PROVIDED AND THE DOCKER IS RUNNING AS STATED ON THE MD FILE
 * </p>
 *
 * @author Niccolo-Francioni
 * @version 1.0
 * @see org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
 * @see org.apache.flink.table.api.bridge.java.StreamTableEnvironment
 * @see org.apache.flink.table.api.TableConfig
 * @see org.apache.flink.types.Row
 * @see org.apache.flink.connector.mongodb.sink.MongoSink
 * @see org.apache.flink.connector.kafka.source.KafkaSource
 * @see org.apache.flink.streaming.api.datastream.DataStream
 * @see com.mongodb.client.model.Filters
 * @see com.mongodb.client.model.InsertOneModel
 * @see com.mongodb.client.model.ReplaceOneModel
 * @see com.mongodb.client.model.ReplaceOptions
 * @see org.flinkfood.schemas.customer.Customer
 * @see org.flinkfood.schemas.customer.KafkaCustomerSchema
 * @see org.flinkfood.schemas.order.KafkaOrderSchema
 * @see org.flinkfood.schemas.order.Order
 */

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

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
                TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "500 ms");
                tableConfig.set("table.exec.mini-batch.size", "1000");
                tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");

                tableEnv.executeSql("CREATE TABLE Customer (\r\n" + //
                                "  id INT,\r\n" + //
                                "  username STRING,\r\n" + //
                                "  first_name STRING,\r\n" + //
                                "  last_name STRING,\r\n" + //
                                "  birthdate STRING,\r\n" + //
                                "  email STRING,\r\n" + //
                                "  fiscal_code STRING,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.customer',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE Orders (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  customer_id INT,\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  supplier_id INT,\r\n" + //
                                "  order_date STRING,\r\n" + //
                                "  payment_date STRING,\r\n" + //
                                "  delivery_date STRING,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  total_amount INT,\r\n" + //
                                "  currency STRING,\r\n" + //
                                "  supply_order BOOLEAN,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.order',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

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

                env.execute("CustomerViewJob");
        }
}

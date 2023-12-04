// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.*;
import org.apache.flink.types.Row;
import static org.apache.flink.table.api.Expressions.*;

import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.flinkfood.schemas.order.KafkaOrderSchema;
import org.flinkfood.schemas.order.Order;
import org.flinkfood.schemas.customer.Customer;
import org.flinkfood.schemas.customer.Customer_address;
import org.flinkfood.schemas.customer.KafkaAddressSchema;
import org.flinkfood.schemas.customer.KafkaCustomerSchema;
import org.flinkfood.serializers.GeneralRowToBsonDocument;

// Class declaration for the Flink job
public class CustomerViewJob {

        // Kafka and MongoDB connection details obtained from environment variables
        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String SOURCE_CUSTOMER_TABLE = "postgres.public.customer";
        private static final String SOURCE_ADDRESS_TABLE = "postgres.public.customer_address";
        private static final String SOURCE_ORDER_TABLE = "postgres.public.order";
        private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
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
                KafkaSource<Customer_address> sourceAddress = KafkaSource.<Customer_address>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_ADDRESS_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaAddressSchema())
                                .build();
                // Setting up Kafka source with relevant configurations
                KafkaSource<Order> sourceOrder = KafkaSource.<Order>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_ORDER_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaOrderSchema())
                                .build();

                // Setting up MongoDB sink with relevant configurations
                MongoSink<Row> sink = MongoSink.<Row>builder()
                                .setUri(MONGODB_URI)
                                .setDatabase(SINK_DB)
                                .setCollection(SINK_DB_TABLE)
                                .setBatchSize(1000)
                                .setBatchIntervalMs(1000)
                                .setMaxRetries(3)
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                                .setSerializationSchema(new GeneralRowToBsonDocument())
                                .build();

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

                DataStream<Customer> streamCustomer = env
                                .fromSource(sourceCustomer, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .setParallelism(1);
                DataStream<Customer_address> streamAddress = env
                                .fromSource(sourceAddress, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .setParallelism(1);

                DataStream<Order> streamOrder = env
                                .fromSource(sourceOrder, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .setParallelism(1);

                Table tableCustomer = tableEnv.fromDataStream(streamCustomer).select($("id").as("id_customer_table"),
                                $("first_name"), $("last_name"), $("birthdate"));

                Table tableAddress = tableEnv.fromDataStream(streamAddress).select(
                                $("id").as("id_address_table"),
                                $("customer_id").as("customer_id_address"), $("street"), $("city"));

                Table tableOrder = tableEnv.fromDataStream(streamOrder)
                                .select($("id").as("id_order_table"),
                                                $("customer_id").as("customer_id_order"), $("name"), $("total_amount"), $("description"),
                                                $("restaurant_id"));

                Table result = tableCustomer.join(tableAddress)
                                .where($("id_customer_table").isEqual($("customer_id_address")))
                                .join(tableOrder)
                                .where($("id_customer_table")
                                                .isEqual($("customer_id_order")))
                                .select($("first_name"),$("last_name"), $("street"), $("city"), $("total_amount"), $("description"),
                                                $("restaurant_id"));

                DataStream<Row> resultStream = tableEnv.toDataStream(result);

                resultStream.print();
                resultStream.sinkTo(sink);

                env.execute("CustomerViewJob");
        }
}

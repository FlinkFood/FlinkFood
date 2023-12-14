// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.jsonArray;
import static org.apache.flink.table.api.Expressions.jsonString;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.mongodb.shaded.org.bson.Document;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ArrayNode;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.flinkfood.schemas.Address;
import org.flinkfood.schemas.Customer;
import org.flinkfood.schemas.KafkaAddressSchema;
import org.flinkfood.schemas.KafkaCustomerSchema;
import org.flinkfood.schemas.KafkaOrderSchema;
import org.flinkfood.schemas.Order;
import org.apache.flink.util.Collector;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

// Class declaration for the Flink job
public class CustomerViewJob {

        // Kafka and MongoDB connection details obtained from environment variables
        private static final String KAFKA_URI = "localhost:9092";
        private static final String SOURCE_CUSTOMER_TABLE = "postgres.public.customer";
        private static final String SOURCE_ADDRESS_TABLE = "postgres.public.address";
        private static final String SOURCE_ORDER_TABLE = "postgres.public.orders";
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
                KafkaSource<Address> sourceAddress = KafkaSource.<Address>builder()
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
                // Setting up MongoDB sink with relevant configurations
                MongoSink<String> sink = MongoSink.<String>builder()
                                .setUri(MONGODB_URI)
                                .setDatabase(SINK_DB)
                                .setCollection(SINK_DB_TABLE)
                                .setBatchSize(1000)
                                .setBatchIntervalMs(1000)
                                .setMaxRetries(3)
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)

                                .setSerializationSchema(
                                                (input, context) -> new InsertOneModel<>(BsonDocument.parse(input)))

                                /*
                                 * .setSerializationSchema((input, context) -> {
                                 * 
                                 * BsonDocument document = BsonDocument.parse(input);
                                 * int idValue = document.getInt32(new String("customer_id")).getValue();
                                 * 
                                 * 
                                 * Bson filter = Filters.eq("customer.id", );
                                 * 
                                 * return new ReplaceOneModel<>(filter, document,
                                 * new ReplaceOptions().upsert(true));
                                 * 
                                 * })
                                 */

                                .build();

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

                DataStream<Customer> streamCustomer = env
                                .fromSource(sourceCustomer, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Customer::getId);

                DataStream<Address> streamAddress = env
                                .fromSource(sourceAddress, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Address::getCustomer_id);

                DataStream<Order> streamOrder = env
                                .fromSource(sourceOrder, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Order::getCustomer_id);

                DataStream<String> resultStream = streamCustomer
                                .keyBy(Customer::getId)
                                .intervalJoin(streamAddress.keyBy(Address::getCustomer_id))
                                .between(Time.seconds(-5), Time.seconds(5)) // Example: 5-second window
                                .lowerBoundExclusive()
                                .upperBoundExclusive()
                                .process(new MyJoinFunction());

                resultStream.print();
                // resultStream.sinkTo(sink);

                env.execute("CustomerViewJob");
        }

        public static class MyJoinFunction extends ProcessJoinFunction<Customer, Address, String> {
                @Override
                public void processElement(Customer customer, Address address, Context context,
                                Collector<String> collector) throws Exception {
                        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode customerNode = mapper.valueToTree(customer);
                        JsonNode addressNode = mapper.valueToTree(address);
                        Document document = new Document();
                        document.append("customer", customerNode);
                        document.append("addresses", addressNode);
                        String json = ow.writeValueAsString(document);
                        collector.collect(json);
                }
        }
}
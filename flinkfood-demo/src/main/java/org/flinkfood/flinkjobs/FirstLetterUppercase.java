package org.flinkfood.flinkjobs;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import com.mongodb.client.model.InsertOneModel;
import org.bson.BsonDocument;

public class FirstLetterUppercase {
        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String SOURCE_DB_TABLE = "dbserver1.inventory.customers";
        private static final String MONGODB_URI = System.getenv("MONGODB_URI");
        private static final String SINK_DB = "flinkfood";
        private static final String SINK_DB_TABLE = "users_sink";

        public static void main(String[] args) throws Exception {
                KafkaSource<String> source = KafkaSource.<String>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_DB_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new SimpleStringSchema())
                                .build();

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
                                .build();

                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

                DataStream<String> stream = env
                                .fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .setParallelism(2);

                stream
                                .map(new MapFunction<String, String>() {
                                        @Override
                                        public String map(String value) throws Exception {
                                                ObjectMapper mapper = new ObjectMapper();
                                                JsonNode msg = mapper
                                                                .readTree(value)
                                                                .path("payload")
                                                                .path("after");

                                                String name = msg.path("first_name").asText();
                                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                                ((ObjectNode) msg).put("first_name", name);

                                                return mapper.writeValueAsString(msg);
                                        }
                                })
                                .setParallelism(1)
                                .sinkTo(sink);

                env.execute("FirstLetterUppercase");
        }
}
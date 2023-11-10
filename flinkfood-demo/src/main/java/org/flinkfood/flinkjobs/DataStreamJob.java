package org.flinkfood.flinkjobs;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import com.mongodb.client.model.InsertOneModel;
import org.bson.BsonDocument;

public class DataStreamJob {
        private static final String MONGODB_URI = System.getenv("MONGODB_URI");
        private static final String DB_NAME = "flinkfood";

        public static void main(String[] args) throws Exception {
                KafkaSource<String> source = null;

                MongoSink<String> sink = MongoSink.<String>builder()
                                .setUri(MONGODB_URI)
                                .setDatabase(DB_NAME)
                                .setCollection("users_sink")
                                .setBatchSize(1000)
                                .setBatchIntervalMs(1000)
                                .setMaxRetries(3)
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                                .setSerializationSchema(
                                                (input, context) -> new InsertOneModel<>(BsonDocument.parse(input)))
                                .build();

                Configuration conf = new Configuration();
                StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

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

                env.execute("MongoDB to MongoBD table transfer");
        }
}
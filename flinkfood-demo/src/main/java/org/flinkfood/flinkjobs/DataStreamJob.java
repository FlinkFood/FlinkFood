package org.flinkfood.flinkjobs;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import com.mongodb.client.model.InsertOneModel;
import org.bson.BsonDocument;

public class DataStreamJob {
        private static final String MONGODB_URI = System.getenv("MONGODB_URI");
        private static final String DB_NAME = "flinkfood";

        public static void main(String[] args) throws Exception {
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

                env.execute("MongoDB to MongoBD table transfer");
        }
}
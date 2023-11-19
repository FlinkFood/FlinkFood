// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
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

// Class declaration for the Flink job
public class ResturantView {

    // Kafka and MongoDB connection details obtained from environment variables
    // TODO: Are these constant over the entire program?
    private static final String KAFKA_URI = "localhost:9092";
    private static final String MONGODB_URI = "mongodb://localhost:27017";

    // TODO: This needs to be changed to the actual table name
    private static final String SOURCE_DB_TABLE = "postgres.public.restaurant";
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_sink";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {

        // Setting up Kafka source with relevant configurations
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_URI)
                .setTopics(SOURCE_DB_TABLE)
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

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
                .build();

        // Setting up Flink execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Creating a data stream from the Kafka source
        DataStream<String> stream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(2);

        // Applying a sample map function to the restuarant
        stream
                .map(new MapFunction<String, String>() {
                    @Override
                    public String map(String value) throws Exception {
                        return value;
                    }
                })
                .setParallelism(1)
                .sinkTo(sink);

        // Execute the Flink job with the given name
        env.execute("ResturantView");
    }
}

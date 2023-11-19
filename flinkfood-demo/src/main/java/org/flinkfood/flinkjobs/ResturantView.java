// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import com.mongodb.client.model.InsertOneModel;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.connector.ChangelogMode;
import org.bson.BsonDocument;

// Class declaration for the Flink job
public class ResturantView {

    // Kafka and MongoDB connection details obtained from environment variables
    private static final String KAFKA_URI = "localhost:9092";
    private static final String MONGODB_URI = "mongodb://localhost:27017";

    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_sink";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        // Setting up Kafka source with relevant configurations
        KafkaSource<String> restaurants_source = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_URI)
                .setTopics("postgres.public.restaurant")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        KafkaSource<String> dishes_source = KafkaSource.<String>builder()
                .setBootstrapServers(KAFKA_URI)
                .setTopics("postgres.public.dishes")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // Creating a data stream from the Kafka source
        DataStream<String> restaurants_stream = env
                .fromSource(restaurants_source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(2);

        Table restaurant_table =
                tEnv.fromDataStream(restaurants_stream).as("ID","NAME", "CITY", "STATE");

        Table dishes_table =
                tEnv.fromDataStream(restaurants_stream).as("ID", "NAME", "PRICE", "RATING", "RESTAURANT_ID");

        // Register Kafka sources as Flink tables
        tEnv.createTemporaryView("restaurants", restaurant_table);
        tEnv.createTemporaryView("dishes", dishes_table);


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

        // SQL query to join the tables
        String sqlQuery = "SELECT r.ID AS restaurant_id, r.NAME AS restaurant_name, r.CITY, r.STATE, " +
                "d.ID AS dish_id, d.NAME AS dish_name, d.PRICE, d.RATING " +
                "FROM restaurants r " +
                "JOIN dishes d ON r.ID = d.RESTAURANT_ID";

        // Execute the SQL query
        Table resultTable = tEnv.sqlQuery(sqlQuery);

        // Convert the result table to a DataStream
        DataStream<String> resultStream = tEnv.toAppendStream(resultTable, String.class);



        DataStream<String> dishes_stream = env
                .fromSource(dishes_source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(2);

        resultStream.print();

        // Execute the Flink job with the given name
        env.execute("ResturantView");
    }
}

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
import com.mongodb.client.model.InsertOneModel;
import org.bson.BsonDocument;
import org.flinkfood.schemas.*;

// Class declaration for the Flink job
public class DishViewJob {

        // Kafka and MongoDB connection details obtained from environment variables
        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String SOURCE_DISH_TABLE = "postgres.public.dishes";
        private static final String SOURCE_RESTAURANT_INFO_TABLE = "postgres.public.restaurant_info";
        private static final String MONGODB_URI = System.getenv("MONGODB_URI");
        private static final String SINK_DB = "flinkfood";
        private static final String SINK_DB_TABLE = "users_sink";

        // Main method where the Flink job is defined
        public static void main(String[] args) throws Exception {

                // Setting up Kafka source with relevant configurations
                KafkaSource<Dish> dishSource = KafkaSource.<Dish>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_DISH_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaDishSchema())
                                .build();

                // Setting up Kafka source with relevant configurations
                KafkaSource<RestaurantInfo> restaurantInfoSource = KafkaSource.<RestaurantInfo>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_RESTAURANT_INFO_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaRestaurantInfoSchema())
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
                                .setSerializationSchema(
                                        // TODO: parse input and store it in MongoDB
                                        // TODO: Determine if insert or update
                                                (input, context) -> new InsertOneModel<>(BsonDocument.parse(input.toString())))
                                .build();

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

                DataStream<Dish> dishStream = env
                                .fromSource(dishSource, WatermarkStrategy.noWatermarks(), "Kafka Dish Source")
                                .setParallelism(1);

                DataStream<RestaurantInfo> restaurantInfoStream = env
                                .fromSource(restaurantInfoSource, WatermarkStrategy.noWatermarks(),
                                                "Kafka Restaurant Info Source")
                                .setParallelism(1);

                Table dishTable = tableEnv.fromDataStream(dishStream).select(
                                $("id").as("dish_id"),
                                $("name").as("dish_name"),
                                $("restaurant_id"));

                Table restaurantInfoTable = tableEnv.fromDataStream(restaurantInfoStream).select(
                                $("id"),
                                $("name").as("restaurant_name"));

                Table result = dishTable
                                .join(restaurantInfoTable)
                                .where($("restaurant_id").isEqual($("id")))
                                .select($("dish_name"), $("dish_id"), $("restaurant_name"));

                DataStream<Row> resultStream = tableEnv.toDataStream(result);

                resultStream.print();
                resultStream.sinkTo(sink);

                env.execute("CustomerViewJob");
        }
}
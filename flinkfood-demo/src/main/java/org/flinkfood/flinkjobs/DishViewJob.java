// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.*;
import org.apache.flink.types.Row;
import static org.apache.flink.table.api.Expressions.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.flinkfood.schemas.*;

// Class declaration for the Flink job
public class DishViewJob {

        // Kafka and MongoDB connection details obtained from environment variables
        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String SOURCE_DISH_TABLE = "postgres.public.dishes";
        private static final String SOURCE_RESTAURANT_INFO_TABLE = "postgres.public.restaurant_info";

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

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

                // Creates a Flink data stream for the dishes
                DataStream<Dish> dishStream = env
                .fromSource(dishSource, WatermarkStrategy.noWatermarks(), "Kafka Dish Source")
                .setParallelism(1);
                
                // Creates a Flink data stream for the restaurants
                DataStream<RestaurantInfo> restaurantInfoStream = env
                                .fromSource(restaurantInfoSource, WatermarkStrategy.noWatermarks(),
                                                "Kafka Restaurant Info Source")
                                .setParallelism(1);

                // Creates a Flink table for the dishes
                Table dishTable = tableEnv.fromDataStream(dishStream).select(
                                $("id").as("dish_id"),
                                $("name").as("dish_name"),
                                $("restaurant_id"));

                // Creates a Flink table for the restaurants
                Table restaurantInfoTable = tableEnv.fromDataStream(restaurantInfoStream).select(
                                $("id"),
                                $("name").as("restaurant_name"));

                // Joins the two tables in a single table and selects some arbitrary fields
                Table result = dishTable
                                .join(restaurantInfoTable)
                                .where($("restaurant_id").isEqual($("id")))
                                .select($("dish_name"), $("dish_id"), $("restaurant_name"));

                // Converts the result table in a Flink data stream 
                DataStream<Row> resultStream = tableEnv.toDataStream(result);
                
                // Print data coming from the stream in the console 
                resultStream.print();
                
                // Starts job execution 
                env.execute("DishViewJob");
        }
}
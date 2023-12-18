// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.*;
import org.apache.flink.types.Row;
import static org.apache.flink.table.api.Expressions.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.flinkfood.schemas.dish.Dish;
import org.flinkfood.schemas.dish.KafkaDishSchema;
import org.flinkfood.schemas.dish_ingredient.DishIngredient;
import org.flinkfood.schemas.dish_ingredient.KafkaDishIngredientSchema;
import org.flinkfood.schemas.ingredient.Ingredient;
import org.flinkfood.schemas.ingredient.KafkaIngredientSchema;
import org.flinkfood.schemas.restaurant.KafkaRestaurantInfoSchema;
import org.flinkfood.schemas.restaurant.RestaurantInfo;
import org.flinkfood.serializers.DishRowToBsonDocument;

// Class declaration for the Flink job
public class DishViewJob {

        // Kafka and MongoDB connection details obtained from environment variables
        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String SOURCE_DISH_TABLE = "postgres.public.dish";
        private static final String SOURCE_RESTAURANT_INFO_TABLE = "postgres.public.restaurant_info";
        private static final String SOURCE_INGREDIENT_TABLE = "postgres.public.ingredient";
        private static final String SOURCE_DISH_INGREDIENT_TABLE = "postgres.public.dish_ingredient";
        private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
        private static final String SINK_DB = "flinkfood";
        private static final String SINK_DB_TABLE = "dish_view";

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

                // Setting up Kafka source with relevant configurations
                KafkaSource<Ingredient> ingredientSource = KafkaSource.<Ingredient>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_INGREDIENT_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaIngredientSchema())
                                .build();

                // Setting up Kafka source with relevant configurations
                KafkaSource<DishIngredient> dishIngredientSource = KafkaSource.<DishIngredient>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_DISH_INGREDIENT_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaDishIngredientSchema())
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

                // Creates a Flink data stream for the ingredients
                DataStream<Ingredient> ingredientStream = env
                                .fromSource(ingredientSource, WatermarkStrategy.noWatermarks(),
                                                "Kafka Restaurant Info Source")
                                .setParallelism(1);

                // Creates a Flink data stream for the dish ingredients
                DataStream<DishIngredient> dishIngredientStream = env
                                .fromSource(dishIngredientSource, WatermarkStrategy.noWatermarks(),
                                                "Kafka Restaurant Info Source")
                                .setParallelism(1);

                // Creates a Flink table for the dishes
                Table dishTable = tableEnv.fromDataStream(dishStream).select(
                                $("id").as("dish_id"),
                                $("name").as("dish_name"),
                                $("price").as("dish_price"),
                                $("currency").as("price_currency"),
                                $("description").as("dish_description"),
                                $("restaurant_id"));

                // Creates a Flink table for the restaurants
                Table restaurantInfoTable = tableEnv
                                .fromDataStream(restaurantInfoStream)
                                .select($("*"));

                // Joins the two tables in a single table and selects some arbitrary fields
                Table result = dishTable
                                .join(restaurantInfoTable)
                                .where($("restaurant_id").isEqual($("id")))
                                .select($("*"))
                                .dropColumns($("restaurant_id"));

                // Creates a Flink table for the ingredients
                Table ingredientTable = tableEnv
                                .fromDataStream(ingredientStream)
                                .select($("*"), $("id").as("ingredient_id"), 
                                $("name").as("ingredient_name"))
                                .dropColumns($("id"), $("name"));

                // Creates a Flink table for the dish_ingredients
                Table dishIngredientTable = tableEnv
                                .fromDataStream(dishIngredientStream)
                                .select($("ingredient_id").as("dish_ingredient_id"),
                                                $("dish_id").as("ingredient_dish_id", args));

                Table result2 = ingredientTable.join(dishIngredientTable)
                                .where($("ingredient_id").isEqual($("dish_ingredient_id")));

                Table finalTable = result.join(result2)
                                .where($("dish_id").isEqual($("ingredient_dish_id")))
                                .dropColumns($("dish_ingredient_id"), $("ingredient_dish_id"));

                // Converts the result table in a Flink data stream
                DataStream<Row> resultStream = tableEnv.toDataStream(finalTable);
                DataStream<Row> resultStream2 = tableEnv.toDataStream(result);

                // resultStream.print();

                MongoSink<Row> sink = MongoSink.<Row>builder()
                                .setUri(MONGODB_URI)
                                .setDatabase(SINK_DB)
                                .setCollection(SINK_DB_TABLE)
                                .setBatchSize(1000)
                                .setBatchIntervalMs(1000)
                                .setMaxRetries(3)
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                                .setSerializationSchema(new DishRowToBsonDocument())
                                .build();

                resultStream.sinkTo(sink);
                resultStream2.sinkTo(sink);

                // Starts job execution
                env.execute("DishViewJob");
        }
}

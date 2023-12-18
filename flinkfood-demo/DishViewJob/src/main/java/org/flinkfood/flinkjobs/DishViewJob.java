// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.*;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
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
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.BsonDocument;

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

                // Setting up MongoDB sink with relevant configurations
                // MongoSink<String> sink = MongoSink.<String>builder()
                //                 .setUri(MONGODB_URI)
                //                 .setDatabase(SINK_DB)
                //                 .setCollection(SINK_DB_TABLE)
                //                 .setBatchSize(1000)
                //                 .setBatchIntervalMs(1000)
                //                 .setMaxRetries(3)
                //                 .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                //                 .setSerializationSchema(
                //                                 (input, context) -> new InsertOneModel<>(BsonDocument.parse(input)))
                //                 .build();

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

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
                TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "5 s");
                tableConfig.set("table.exec.mini-batch.size", "5000");

                // Creates a Flink data stream for the dishes
                DataStream<Dish> dishStream = env
                                .fromSource(dishSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Dish::getId);
                // Creates a table for the dishes
                Table dishTable = tableEnv.fromDataStream(dishStream);
                // Creates a temporary view for the dishes
                tableEnv.createTemporaryView("Dish", tableEnv.toChangelogStream(dishTable));

                DataStream<DishIngredient> streamDishIngredient = env
                                .fromSource(dishIngredientSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(DishIngredient::getDish_id);
                Table dishIngredientTable = tableEnv.fromDataStream(streamDishIngredient);
                tableEnv.createTemporaryView("DishIngredients", tableEnv.toChangelogStream(dishIngredientTable));
                
                DataStream<Ingredient> streamIngredient = env
                                .fromSource(ingredientSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Ingredient::getId);
                Table ingredientTable = tableEnv.fromDataStream(streamIngredient);
                tableEnv.createTemporaryView("Ingredients", tableEnv.toChangelogStream(ingredientTable));
                
                DataStream<RestaurantInfo> streamRestaurants = env
                                .fromSource(restaurantInfoSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(RestaurantInfo::getId);
                Table restaurantTable = tableEnv.fromDataStream(streamRestaurants);
                tableEnv.createTemporaryView("Restaurants", tableEnv.toChangelogStream(restaurantTable));

                tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");

                // Query that returns a JSON
                Table resultTable = tableEnv
                .sqlQuery(
                                "SELECT DISTINCT " +
                                                "JSON_OBJECT('dish_id' VALUE d.id, 'dish_name' VALUE d.name, 'dish_description' VALUE d.description, "
                                                +
                                                "'served_in' VALUE JSON_OBJECT(" +
                                                "'restaurant_id' VALUE r.id, 'name' VALUE r.name, 'email' VALUE r.email, " +
                                                "'phone' VALUE r.phone, 'vat_code' VALUE r.vat_code, 'price_range' VALUE r.price_range), " 
                                                +
                                                "'ingredients' VALUE ARRAY_AGGR(JSON_OBJECT(" +
                                                "'ingredient_id' VALUE i.id, 'name' VALUE i.name, 'description' VALUE i.description)))"
                                                +
                                                "as dish_view " +
                                                "FROM Dish d " +
                                                "JOIN Restaurants r ON d.restaurant_id = r.id " +
                                                "LEFT JOIN DishIngredients di ON d.id = di.dish_id " +
                                                "LEFT JOIN Ingredients i ON di.ingredient_id = i.id " +
                                                "GROUP BY d.id, d.name, d.description, r.id, r.name, r.email, r.phone, r.vat_code, r.price_range;");

                tableEnv.toChangelogStream(resultTable).sinkTo(sink);
                // process(new ProcessFunction<Row, String>() {
                //         @Override
                //         public void processElement(
                //                         Row row,
                //                         ProcessFunction<Row, String>.Context context,
                //                         Collector<String> out) {
                //                 System.out.println("ROW: " + row.getField(0).toString());
                //                 out.collect(row.getField(0).toString());
                //         }
                // }).sinkTo(sink);

                // Starts job execution
                env.execute("DishViewJob");
        }
}

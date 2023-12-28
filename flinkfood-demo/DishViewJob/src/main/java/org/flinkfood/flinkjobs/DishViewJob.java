// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

// Class declaration for the Flink job
public class DishViewJob {

        // Main method where the Flink job is defined
        public static void main(String[] args) throws Exception {

                // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
                TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "500 ms");
                tableConfig.set("table.exec.mini-batch.size", "1000");
                tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");

                tableEnv.executeSql("CREATE TABLE Dish (\r\n" + //
                                "  id INT,\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  price INT,\r\n" + //
                                "  currency STRING,\r\n" + //
                                "  category STRING,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.dish',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE Ingredients (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  carbs INT,\r\n" + //
                                "  proteins INT,\r\n" + //
                                "  fats INT,\r\n" + //
                                "  fibers INT,\r\n" + //
                                "  salt INT,\r\n" + //
                                "  calories INT,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.ingredient',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE DishIngredients (\r\n" + //
                                "  id INT,\r\n" + //
                                "  dish_id INT,\r\n" + //
                                "  ingredient_id INT,\r\n" + //
                                "  supplier_id INT,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.dish_ingredient',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE Restaurants (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  phone STRING,\r\n" + //
                                "  email STRING,\r\n" + //
                                "  cuisine_type STRING,\r\n" + //
                                "  price_range STRING,\r\n" + //
                                "  vat_code INT,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.restaurant_info',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE Reviews (\r\n" + //
                                "  id INT,\r\n" + //
                                "  dish_id INT,\r\n" + //
                                "  customer_id INT,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  rating INT,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.reviews_dish',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE DishView (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  price INT,\r\n" + //
                                "  currency STRING,\r\n" + //
                                "  category STRING,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  ingredients ARRAY<row<id INT, name STRING, description STRING, carbs INT, fats INT, fibers INT, proteins INT, salt INT, calories INT>>,\r\n" + //
                                "  served_in ARRAY<row<id INT, name STRING, email STRING, phone STRING, cuisine_type STRING, price_range STRING, vat_code INT>>,\r\n" + //
                                "  reviews ARRAY<row<id INT, customer_id INT, rating INT, description STRING>>,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "   'connector' = 'mongodb',\r\n" + //
                                "   'uri' = 'mongodb://localhost:27017',\r\n" + //
                                "   'database' = 'flinkfood',\r\n" + //
                                "   'collection' = 'dish_views'\r\n" + //
                                ");");

                tableEnv.executeSql("INSERT INTO DishView\n" + //
                                "SELECT DISTINCT\n" + //
                                "    d.id,\n" + //
                                "    d.name,\n" + //
                                "    d.price,\n" + //
                                "    d.currency,\n" + //
                                "    d.category,\n" + //
                                "    d.description,\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(di.id, i.name, i.description, i.carbs, i.fats, i.fibers, i.proteins, i.salt, i.calories)\n" + //
                                "    ) FROM DishIngredients di\n" + //
                                "    LEFT JOIN Ingredients i ON di.ingredient_id = i.id\n" + //
                                "    WHERE di.dish_id = d.id),\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(r.id, r.name, r.email, r.phone, r.cuisine_type, r.price_range, r.vat_code)\n" + //
                                "    ) FROM Restaurants r\n" + //
                                "    WHERE r.id = d.restaurant_id),\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(rw.id, rw.customer_id, rw.rating, rw.description)\n" + //
                                "    ) FROM Reviews rw\n" + //
                                "    WHERE rw.dish_id = d.id)\n" + //
                                "FROM Dish d;");

                // Starts job execution
                env.execute("DishViewJob");
        }
}

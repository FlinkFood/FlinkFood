// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * The {@code RetaurantView} class defines a Flink job responsible for processing the DishView, one of the 3 main views.
 *
 * <p>The job initializes the Flink execution environment, sets necessary configurations, and creates multiple tables 
 * using Flink's Table API and Flink SQL. These tables are defined with Apache Kafka as the source connector and Debezium JSON format.
 * The tables represent entities like dishes, ingredients, restaurants, reviews, and a view combining dish details 
 * with associated ingredients, restaurants, and reviews.</p>
 *
 * <p>The job concludes by populating the {@code RestaurantView} table which aggregates data from the previously defined tables.
 *
 *
 * <p>Note: Ensure that the Apache Kafka and MongoDB services are running on the docker as stated in the HowToRun.md instructions.</p>
 *
 * @author lucamozzz
 * @version 1.0
 * @see StreamExecutionEnvironment
 * @see StreamTableEnvironment
 * @see TableConfig
 * @see ArrayAggr
 */

// Class declaration for the Flink job
public class RestaurantView {

        private static final String KAFKA_URI = System.getenv("KAFKA_URI");
        private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
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
                                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

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
                                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE RestaurantAddress (\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  street STRING,\r\n" + //
                                "  address_number CHAR,\r\n" + //
                                "  zip_code INT,\r\n" + //
                                "  city STRING,\r\n" + //
                                "  province STRING,\r\n" + //
                                "  country STRING,\r\n" + //
                                "  PRIMARY KEY (restaurant_id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.restaurant_address',\r\n" + //
                                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE RestaurantService (\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  take_away BOOLEAN,\r\n" + //
                                "  delivery BOOLEAN,\r\n" + //
                                "  dine_in BOOLEAN,\r\n" + //
                                "  parking_lots INT,\r\n" + //
                                "  accessible BOOLEAN,\r\n" + //
                                "  children_area BOOLEAN,\r\n" + //
                                "  children_food BOOLEAN,\r\n" + //
                                "  PRIMARY KEY (restaurant_id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.restaurant_service',\r\n" + //
                                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE RestaurantReview (\r\n" + //
                                "  id INT,\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  customer_id INT,\r\n" + //
                                "  rating INT,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.restaurant_review',\r\n" + //
                                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE RestaurantView (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  phone STRING,\r\n" + //
                                "  email STRING,\r\n" + //
                                "  cuisine_type STRING,\r\n" + //
                                "  price_range STRING,\r\n" + //
                                "  vat_code INT,\r\n" + //
                                "  reviews ARRAY<row<id INT, restaurant_id INT, customer_id INT, rating INT, description STRING>>,\r\n" + //
                                "  served_dishes ARRAY<row<id INT, name STRING, price INT, currency STRING, category STRING, description STRING>>,\r\n" + //
                                "  branches ARRAY<row<restaurant_id INT, street STRING, address_number CHAR, zip_code INT, city STRING, province STRING, country STRING>>,\r\n" + //
                                "  services ARRAY<row<take_away BOOLEAN, delivery BOOLEAN, dine_in BOOLEAN, parking_lots INT, accessible BOOLEAN, children_area BOOLEAN, children_food BOOLEAN>>,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "   'connector' = 'mongodb',\r\n" + //
                                "   'uri' = '" + MONGODB_URI + "',\r\n" + //
                                "   'database' = 'flinkfood',\r\n" + //
                                "   'collection' = 'restaurant_views'\r\n" + //
                                ");");

                tableEnv.executeSql("INSERT INTO RestaurantView\n" + //
                                "SELECT DISTINCT\n" + //
                                "    r.id,\n" + //
                                "    r.name,\n" + //
                                "    r.phone,\n" + //
                                "    r.email,\n" + //
                                "    r.cuisine_type,\n" + //
                                "    r.price_range,\n" + //
                                "    r.vat_code,\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(rr.id, rr.restaurant_id, rr.customer_id, rr.rating, rr.description)\n" + //
                                "    ) FROM RestaurantReview rr\n" + //
                                "    WHERE rr.restaurant_id = r.id),\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(d.id, d.name, d.price, d.currency, d.category, d.description)\n" + //
                                "    ) FROM Dish d\n" + //
                                "    WHERE r.id = d.restaurant_id),\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(ra.restaurant_id, ra.street, ra.address_number, ra.zip_code, ra.city, ra.province, ra.country)\n" + //
                                "    ) FROM RestaurantAddress ra\n" + //
                                "    WHERE ra.restaurant_id = r.id),\n" + //
                                "    (SELECT ARRAY_AGGR(\n" + //
                                "        ROW(rs.take_away, rs.delivery, rs.dine_in, rs.parking_lots, rs.accessible, rs.children_area, rs.children_food)\n" + //
                                "    ) FROM RestaurantService rs\n" + //
                                "    WHERE rs.restaurant_id = r.id)\n" + //
                                "FROM Restaurants r;");

                // Starts job execution
                env.execute("RestaurantViewJob");
        }
}

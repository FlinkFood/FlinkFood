// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;


// Class declaration for the Flink job
public class ResturantView {

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        // Define the restaurant_info table
        tEnv.executeSql("CREATE TABLE restaurant_info (\n" +
        " id BIGINT,\n" +
        " name STRING,\n" +
        " address STRING,\n" +
        " phone STRING,\n" +
        " email STRING,\n" +
        " price_range STRING,\n" +
        " cuisine_type STRING,\n" +
        " vat_code INT\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_info',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        // Define the restaurant_services table
        tEnv.executeSql("CREATE TABLE restaurant_services (\n" +
        " restaurant_id INT,\n" +
        " take_away BOOLEAN,\n" +
        " delivery BOOLEAN,\n" +
        " dine_in BOOLEAN,\n" +
        " parking_lots INT,\n" +
        " accessible BOOLEAN,\n" +
        " children_area BOOLEAN,\n" +
        " children_food BOOLEAN\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_services',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        // Define the restaurant_address table
        tEnv.executeSql("CREATE TABLE restaurant_address (\n" +
        " restaurant_id INT,\n" +
        " street STRING,\n" +
        " address_number STRING,\n" +
        " zip_code INT,\n" +
        " city STRING,\n" +
        " province STRING,\n" +
        " country STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_address',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        // Define the restaurant_reviews table
        tEnv.executeSql("CREATE TABLE restaurant_reviews (\n" +
        " id INT,\n" +
        " restaurant_id INT,\n" +
        " customer_id INT,\n" +
        " rating DECIMAL(3,1),\n" +
        " review_comment STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_reviews',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        // Define the dishes table
        tEnv.executeSql("CREATE TABLE dishes (\n" +
        " id BIGINT,\n" +
        " restaurant_id INT,\n" +
        " name STRING,\n" +
        " price DECIMAL(5,2),\n" +
        " currency STRING,\n" +
        " category STRING,\n" +
        " description STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.dishes',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        // Define the review_dish table
        tEnv.executeSql("CREATE TABLE review_dish (\n" +
        " id INT,\n" +
        " dish_id INT,\n" +
        " customer_id INT,\n" +
        " rating DECIMAL(3,1),\n" +
        " review_comment STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.review_dish',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        String sqlQuery = "SELECT r.id AS restaurant_id, r.name AS restaurant_name, " +
        "s.take_away AS take_away, s.delivery AS delivery " +
        "FROM restaurant_info r " +
        "JOIN restaurant_services s ON r.id = s.restaurant_id";

        Table resultTable = tEnv.sqlQuery(sqlQuery);

        // Convert the result table to a DataStream
        DataStream<Row> resultStream = tEnv.toDataStream(resultTable);

        resultStream.print();

        //Execute the Flink job with the given name
        env.execute("ResturantView");
    }
}

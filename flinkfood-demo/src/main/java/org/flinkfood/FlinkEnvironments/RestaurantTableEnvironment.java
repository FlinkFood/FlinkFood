package org.flinkfood.FlinkEnvironments;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class RestaurantTableEnvironment {
    private StreamTableEnvironment tEnv;

    public RestaurantTableEnvironment(StreamExecutionEnvironment env) {
        this.tEnv = StreamTableEnvironment.create(env);
    }

    public void createRestaurantInfoTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_info (\n" +
        " id INT,\n" +
        " name STRING,\n" +
        " phone STRING,\n" +
        " email STRING,\n" +
        " cuisine_type STRING,\n" +
        " price_range STRING,\n" +
        " vat_code INT\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_info',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'restaurant_info_group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public void createRestaurantServicesTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_services (\n" +
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
        " 'properties.group.id' = 'restaurant_services_group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public void createRestaurantAddressTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_address (\n" +
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
        " 'properties.group.id' = 'restaurant_address_group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public void createRestaurantReviewsTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_reviews (\n" +
        " id INT,\n" +
        " restaurant_id INT,\n" +
        " customer_id INT,\n" +
        " rating STRING,\n" +
        " review_comment STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant_reviews',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'restaurant_reviews_group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public void createDishesTable() {
        this.tEnv.executeSql("CREATE TABLE dishes (\n" +
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
        " 'properties.group.id' = 'dishes-group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public void createReviewDishTable() {
        this.tEnv.executeSql("CREATE TABLE review_dish (\n" +
        " id INT,\n" +
        " dish_id INT,\n" +
        " customer_id INT,\n" +
        " rating DECIMAL(3,1),\n" +
        " review_comment STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.review_dish',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'review_dish_group',\n" +
        " 'format' = 'debezium-json',\n" +
        " 'debezium-json.schema-include' = 'true',\n" +
        " 'scan.startup.mode' = 'earliest-offset',\n" +
        " 'properties.auto.offset.reset' = 'earliest'\n" +
        ")");
    }

    public Table createUnifiedRestaurantView() {
        String joinQuery = "SELECT * FROM restaurant_info r " +
        "JOIN restaurant_services s ON r.id = s.restaurant_id " +
        "JOIN restaurant_address a ON r.id = a.restaurant_id " +
        "JOIN restaurant_reviews rv ON r.id = rv.restaurant_id";

        Table result = tEnv.sqlQuery(joinQuery);
        return result;
    }

    public DataStream<Row> toDataStream(Table unifiedRestaurantTable) {
        DataStream<Row> resultStream = tEnv.toChangelogStream(unifiedRestaurantTable);
        return resultStream;
    }

    public StreamTableEnvironment getTableEnvironment() {
        return this.tEnv;
    }
}

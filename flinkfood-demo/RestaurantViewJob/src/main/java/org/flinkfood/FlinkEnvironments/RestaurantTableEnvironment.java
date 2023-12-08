package org.flinkfood.FlinkEnvironments;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class RestaurantTableEnvironment {
    private final StreamTableEnvironment tEnv;
    private static final String KAFKA_URI = System.getenv("KAFKA_URI");

    public RestaurantTableEnvironment(StreamExecutionEnvironment env) {
        this.tEnv = StreamTableEnvironment.create(env);
    }

    public void createRestaurantInfoTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_info (" +
        " id INT," +
        " name STRING," +
        " phone STRING," +
        " email STRING," +
        " cuisine_type STRING," +
        " price_range STRING," +
        " vat_code INT" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.restaurant_info'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public void createRestaurantServicesTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_service (" +
        " restaurant_id INT," +
        " take_away BOOLEAN," +
        " delivery BOOLEAN," +
        " dine_in BOOLEAN," +
        " parking_lots INT," +
        " accessible BOOLEAN," +
        " children_area BOOLEAN," +
        " children_food BOOLEAN" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.restaurant_service'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public void createRestaurantAddressTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_address (" +
        " restaurant_id INT," +
        " street STRING," +
        " address_number STRING," +
        " zip_code INT," +
        " city STRING," +
        " province STRING," +
        " country STRING" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.restaurant_address'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public void createRestaurantReviewsTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_review (" +
        " id INT," +
        " restaurant_id INT," +
        " customer_id INT," +
        " rating STRING," +
        " review_comment STRING" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.restaurant_review'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public void createDishesTable() {
        this.tEnv.executeSql("CREATE TABLE dish (" +
        " id BIGINT," +
        " restaurant_id INT," +
        " name STRING," +
        " price SMALLINT," +
        " currency STRING," +
        " category STRING," +
        " description STRING" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.dish'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public void createReviewDishTable() {
        this.tEnv.executeSql("CREATE TABLE review_dish (" +
        " id INT," +
        " dish_id INT," +
        " customer_id INT," +
        " rating SMALLINT," +
        " review_comment STRING" +
        ") WITH (" +
        " 'connector' = 'kafka'," +
        " 'topic' = 'postgres.public.review_dish'," +
        " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
        " 'format' = 'json'," +
        " 'scan.startup.mode' = 'earliest-offset'," +
        " 'properties.auto.offset.reset' = 'earliest'" +
        ")");
    }

    public StreamTableEnvironment gettEnv() {
        return tEnv;
    }

    public Table createSimpleUnifiedRestaurantView() {
        String joinQuery =
                "SELECT * " +
                "FROM ( " +
                " SELECT " +
                " r.id AS id, " +
                " r.name AS name, " +
                " a.street as street, " +
                " a.address_number as number, " +
                " a.zip_code AS zipCode, " +
                " a.city AS city, " +
                " a.province AS province, " +
                " a.country AS country, " +
                " r.vat_code AS vatCode, " +
                " r.email AS email, " +
                " s.take_away AS takeAway, " +
                " s.delivery AS delivery, " +
                " s.dine_in AS dineIn, " +
                " s.parking_lots AS parkingLots, " +
                " s.accessible AS accessibleEntrance, " +
                " s.children_area AS childrenArea, " +
                " s.children_food AS childrenFood " +
                " FROM restaurant_info r " +
                " INNER JOIN restaurant_service s ON r.id = s.restaurant_id " +
                " INNER JOIN restaurant_address a ON r.id = a.restaurant_id " +
                ") AS view";
        tEnv. sqlQuery(joinQuery)
                .execute();
        return tEnv.from("view");
    }

    public Table getRestaurantReviews(int id) {
        String query = "SELECT * FROM restaurant_reviews WHERE restaurant_id = " + id;
        return this.tEnv.sqlQuery(query);
    }

    public DataStream<Row> toDataStream(Table unifiedRestaurantTable) {
        DataStream<Row> dsRow = tEnv.toChangelogStream(unifiedRestaurantTable);
        return dsRow;
    }
}

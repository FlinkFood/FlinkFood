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

        // Define the tables in TableEnvironment
        tEnv.executeSql("CREATE TABLE restaurants (\n" +
        " ID STRING,\n" +
        " NAME STRING,\n" +
        " CITY STRING,\n" +
        " STATE STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.restaurant',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        tEnv.executeSql("CREATE TABLE dishes (\n" +
        " ID STRING,\n" +
        " NAME STRING,\n" +
        " PRICE DOUBLE,\n" +
        " RATING INT,\n" +
        " RESTAURANT_ID STRING\n" +
        ") WITH (\n" +
        " 'connector' = 'kafka',\n" +
        " 'topic' = 'postgres.public.dishes',\n" +
        " 'properties.bootstrap.servers' = 'localhost:9092',\n" +
        " 'properties.group.id' = 'my-group',\n" +
        " 'format' = 'json'\n" +
        ")");

        String sqlQuery = "SELECT r.ID AS restaurant_id, r.NAME AS restaurant_name, r.CITY, r.STATE, " +
        "d.ID AS dish_id, d.NAME AS dish_name, d.PRICE, d.RATING " +
        "FROM restaurants r " +
        "JOIN dishes d ON r.ID = d.RESTAURANT_ID";

        Table resultTable = tEnv.sqlQuery(sqlQuery);

        // Convert the result table to a DataStream
        DataStream<Row> resultStream = tEnv.toDataStream(resultTable);

        resultStream.print();

        //Execute the Flink job with the given name
        env.execute("ResturantView");
    }
}

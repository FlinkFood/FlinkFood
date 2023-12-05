package org.flinkfood.FlinkEnvironments;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class CustomerTableEnvironment {
    private final StreamTableEnvironment tEnv;
    private static final String KAFKA_URI = System.getenv("KAFKA_URI");

    public CustomerTableEnvironment(StreamExecutionEnvironment env) {
        this.tEnv = StreamTableEnvironment.create(env);
    }

    public void createCustomerTable() {
        this.tEnv.executeSql("CREATE TABLE customer (" +
                " id INT," +
                " username STRING," +
                " first_name STRING," +
                " last_name STRING," +
                " birthdate INT," + //DEBEZIUM received the date type as INTEGER
                " email STRING," +
                " fisca_code STRING" +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.customer'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    public void createCustomer_addressTable() {
        this.tEnv.executeSql("CREATE TABLE restaurant_service (" +
                " id INT," +
                " customer_id INT," +
                " street STRING," +
                " address_number INT," +
                " zip_code INT," +
                " city STRING," +
                " province STRING," +
                " COUNTRY STRING" +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.customer_address'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    public Table createSimpleUnifiedRestaurantView() {
        String joinQuery =
                "SELECT * " +
                        " FROM customer c " +
                        " INNER JOIN customer_address a ON a.customer_id = c.id ";
        return this.tEnv.sqlQuery(joinQuery);
    }

    public DataStream<Row> toDataStream(Table unifiedCustomerTables) {
        DataStream<Row> dsRow = tEnv.toChangelogStream(unifiedCustomerTables);
        return dsRow;
    }
}
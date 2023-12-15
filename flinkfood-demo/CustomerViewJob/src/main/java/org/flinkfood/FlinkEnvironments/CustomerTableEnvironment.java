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
                " birthdate INT," + //DEBEZIUM receives the date type as INTEGER
                " email STRING," +
                " fiscal_code STRING" +
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
        this.tEnv.executeSql("CREATE TABLE customer_address (" +
                " id INT," +
                " customer_id INT," +
                " street STRING," +
                " address_number STRING," +
                " zip_code INT," +
                " city STRING," +
                " province STRING," +
                " country STRING" +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.customer_address'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    public void createPayment_methodTable() {
        this.tEnv.executeSql("CREATE TABLE payment_method (" +
                " id INT," +
                " customer_id INT," +
                " name STRING" +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.payment_method'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    public void createOrderTable() {    //"order" is a reserved word, so it can't be used
        this.tEnv.executeSql("CREATE TABLE orderName (" +
                " id INT," +
                " name STRING," +
                " customer_id INT," +
                " restaurant_id INT," +
                " supplier_id INT," +
                " order_date INT," +
                " payment_date INT," +
                " delivery_date INT," +
                " description STRING," +
                " total_amount INT," +
                " currency STRING," +
                " supply_order BOOLEAN " +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.order'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    public Table createFinalCustomerView()
    {   
        String joinQuery = 
                    "SELECT * FROM "+
                        "(SELECT c.username AS username, "+
                        "c.first_name AS first_name, "+
                        "c.last_name AS last_name, " +
                        "DATE_FORMAT(FROM_UNIXTIME(c.birthdate*86400),'dd-MM-yyyy') AS birthdate, " +
                        "c.email AS email, "+
                        "a.country AS country, "+
                        "a.province AS province, "+
                        "a.street AS street, "+
                        "a.address_number AS addressNumber, "+
                        "p.name AS paymentName, "+
                        "o.restaurant_id AS restaurantID, "+
                        "FROM_UNIXTIME(o.order_date*86400) AS orderDate, "+
                        "FROM_UNIXTIME(o.payment_date*86400) AS paymentDate, "+
                        "FROM_UNIXTIME(o.delivery_date*86400) AS deliveryDate, "+
                        "o.description AS orderDescription, "+
                        "o.total_amount AS totalAmount "+
                        "FROM customer c "+
                        "INNER JOIN customer_address a ON a.customer_id = c.id "+
                        "INNER JOIN payment_method p ON p.customer_id = c.id "+
                        "INNER JOIN orderName o ON o.customer_id = c.id) "+
                        "AS subquery";

        return this.tEnv.sqlQuery(joinQuery);
    }

    public DataStream<Row> toDataStream(Table unifiedCustomerTables) {
        DataStream<Row> dsRow = tEnv.toChangelogStream(unifiedCustomerTables);
        return dsRow;
    }
}
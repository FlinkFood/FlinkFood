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

    public void createOrderTable() {
        this.tEnv.executeSql("CREATE TABLE order (" +
                " id INT," +
                " name STRING," +
                " customer_id INT," +
                " restaurant_id INT," +
                " supplier_id INT," +
                " order_date INT," +
                " payment_date INT," +
                " delivery_date INT," +
                " description INT," +
                " total_amount INT," +
                " currency STRING," +
                " supply_order BOOLEAN," +
                ") WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'postgres.public.order'," +
                " 'properties.bootstrap.servers' = '"+ KAFKA_URI +"'," +
                " 'format' = 'json'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'properties.auto.offset.reset' = 'earliest'" +
                ")");
    }

    //TO-DO FIX mongoDB, probably renaming each field
    public Table createCustomerView() {
        String joinQuery =  "SELECT "+
                "(customer.username,"+
                "customer.first_name, customer.last_name, " +
                "DATE_FORMAT(FROM_UNIXTIME(customer.birthdate*86400),'dd-MM-yyyy')," +
                "customer.email, customer_address.country,"+
                "customer_address.province,"+
                "customer_address.street, customer_address.address_number,"+
                "payment_method.name)"+
                "FROM customer, customer_address, payment_method "+
                "WHERE (customer.id = customer_address.customer_id "+
                        "AND customer.id = payment_method.customer_id)";
        return this.tEnv.sqlQuery(joinQuery);
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
                        "a.address_number AS addressNumber "+
                        "FROM customer c "+
                        "INNER JOIN customer_address a ON a.customer_id = c.id) "+
                        "AS subquery";

        return this.tEnv.sqlQuery(joinQuery);
    }

    public Table testQuery() {
        String joinQuery =
                "SELECT * FROM "+
                        "(SELECT DATE_FORMAT(FROM_UNIXTIME(c.birthdate*86400),'dd-MM-yyyy') AS birthdate,"+
                        "a.street AS street FROM customer c "+
                        "INNER JOIN customer_address a ON a.customer_id = c.id)"+
                        "AS subquery";
        return this.tEnv.sqlQuery(joinQuery);
    }

    public Table simpleCustomerView()
    {
        String joinQuery =  "SELECT id FROM customer";
        return this.tEnv.sqlQuery(joinQuery);
    }


    public DataStream<Row> toDataStream(Table unifiedCustomerTables) {
        DataStream<Row> dsRow = tEnv.toChangelogStream(unifiedCustomerTables);
        return dsRow;
    }
}
package org.flinkfood.FlinkEnvironments;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public final class CustomerEnvironment {

    private static final String KAFKA_URI = System.getenv("KAFKA_URI");
    private static CustomerEnvironment INSTANCE;
    private StreamExecutionEnvironment env;
    private StreamTableEnvironment tableEnv;
    private TableConfig tableConfig;

    private CustomerEnvironment() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        tableEnv = StreamTableEnvironment.create(env);
        tableConfig = tableEnv.getConfig();
        tableConfig.set("table.exec.mini-batch.enabled", "true");
        tableConfig.set("table.exec.mini-batch.allow-latency", "500 ms");
        tableConfig.set("table.exec.mini-batch.size", "1000");
        tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");
    }

    public static CustomerEnvironment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerEnvironment();
            INSTANCE.registerCustomerTable();
            INSTANCE.registerCustomerAddressTable();
            INSTANCE.registerDishTable();
            INSTANCE.registerOrderTable();
            INSTANCE.registerPaymentMethodTable();
            INSTANCE.registerRestaurantInfoTable();
            INSTANCE.registerRestaurantReviewTable();
            INSTANCE.registerReviewDishTable();
        }

        return INSTANCE;
    }

    public StreamExecutionEnvironment getEnv() {
        return env;
    }

    public StreamTableEnvironment getTableEnv() {
        return tableEnv;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    private void registerCustomerTable() {
        tableEnv.executeSql("CREATE TABLE Customer (\r\n" + //
                "  id INT,\r\n" + //
                "  username STRING,\r\n" + //
                "  first_name STRING,\r\n" + //
                "  last_name STRING,\r\n" + //
                "  birthdate STRING,\r\n" + //
                "  email STRING,\r\n" + //
                "  fiscal_code STRING,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.customer',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");
    }

    private void registerOrderTable() {
        tableEnv.executeSql("CREATE TABLE Orders (\r\n" + //
                "  id INT,\r\n" + //
                "  name STRING,\r\n" + //
                "  customer_id INT,\r\n" + //
                "  restaurant_id INT,\r\n" + //
                "  supplier_id INT,\r\n" + //
                "  order_date STRING,\r\n" + //
                "  payment_date STRING,\r\n" + //
                "  delivery_date STRING,\r\n" + //
                "  description STRING,\r\n" + //
                "  total_amount INT,\r\n" + //
                "  currency STRING,\r\n" + //
                "  supply_order BOOLEAN,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.order',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");

    }

    private void registerCustomerAddressTable() {
        tableEnv.executeSql("CREATE TABLE Customer_address (\r\n" + //
                "  id INT,\r\n" + //
                "  customer_id INT,\r\n" + //
                "  street STRING,\r\n" + //
                "  address_number STRING,\r\n" + //
                "  zip_code INT,\r\n" + //
                "  city STRING,\r\n" + //
                "  province STRING,\r\n" + //
                "  country STRING,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.customer_address',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");

    }

    private void registerPaymentMethodTable() {
        tableEnv.executeSql("CREATE TABLE Payment_method (\r\n" + //
                "  id INT,\r\n" + //
                "  customer_id INT,\r\n" + //
                "  name STRING,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.payment_method',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");
    }

    private void registerReviewDishTable() {
        tableEnv.executeSql("CREATE TABLE Review_dish (\r\n" + //
                "  id INT,\r\n" + //
                "  dish_id INT,\r\n" + //
                "  customer_id INT,\r\n" + //
                "  rating INT,\r\n" + //
                "  review STRING,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.reviews_dish',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");
    }

    private void registerRestaurantReviewTable() {
        tableEnv.executeSql("CREATE TABLE Restaurant_review (\r\n" + //
                "  id INT,\r\n" + //
                "  restaurant_id INT,\r\n" + //
                "  customer_id INT,\r\n" + //
                "  rating INT,\r\n" + //
                "  review STRING,\r\n" + //
                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                ") WITH (\r\n" + //
                "  'connector' = 'kafka',\r\n" + //
                "  'topic' = 'postgres.public.restaurant_review',\r\n" + //
                "  'properties.bootstrap.servers' = '" + KAFKA_URI + "',\r\n" + //
                "  'properties.group.id' = 'testGroup', \r\n" + //
                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                "  'format' = 'debezium-json'\r\n" + //
                ");");
    }

    private void registerRestaurantInfoTable() {
        tableEnv.executeSql("CREATE TABLE Restaurant_info (\r\n" + //
                "  id INT,\r\n" + //
                "  name STRING,\r\n" + //
                "  phone STRING,\r\n" + //
                "  email STRING,\r\n" + //
                "  cusine_type STRING,\r\n" + //
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

    }

    private void registerDishTable() {
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
    }

}

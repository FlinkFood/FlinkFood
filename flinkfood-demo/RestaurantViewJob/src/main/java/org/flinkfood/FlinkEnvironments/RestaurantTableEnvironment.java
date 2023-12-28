package org.flinkfood.FlinkEnvironments;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import org.flinkfood.supportClasses.YAML_table;
import org.flinkfood.supportClasses.YAML_reader;

/**
 * The {@code RestaurantTableEnvironment} class provides support for creating a table environment for the RestaurantView.
 * It facilitates the creation of tables based on configurations read from the YAML file.
 * 
 * This Java file that connects the flink with the kafka topics, that will be used to create the restaurant view
 *
 * This class has an instance variable tEnv of type StreamTableEnvironment, which is used for creating and executing Flink SQL queries on streaming tables
 * 
 * The table schemas are hardcoded within the class. The configurations for tables are read from a YAML file.
 * Each table is associated with a Kafka topic for streaming purposes.
 *
 * The class provides methods to retrieve all defined tables, execute SQL queries directly on the table environment,
 * and execute insert operations on the table environment.
 * 
 * Each of these methods uses the **`executeSql`** method of the **`StreamTableEnvironment`** to create tables by executing SQL DDL (Data Definition Language) 
 * statements. These tables are expected to be **backed by Kafka connectors**, as indicated by the provided connector and topic configurations.
 *
 * @author PolimiGiovanniArriciati
 * @version 1.0
 */

public class RestaurantTableEnvironment {
    private final StreamTableEnvironment tEnv;
    private static final String KAFKA_URI = "localhost:9092";
    private ArrayList<YAML_table> tables;

    /**
     * Support class for the creation of the table environment
     * Tables schemas are hardcoded in the class
     */

    public RestaurantTableEnvironment(StreamExecutionEnvironment env, String config_file) throws FileNotFoundException {
        this.tEnv = StreamTableEnvironment.create(env);
        createAllTables(config_file);
    }

    private void createAllTables(String config_file) throws FileNotFoundException {
        tables = (new YAML_reader(config_file)).readYamlFile();
        for (YAML_table table : tables) {
            String query;
            query = "CREATE TABLE " + table.getName() + "(" + table.getSchema() + ")" +
                    " WITH (" +
                    " 'connector' = 'kafka'," +
                    " 'topic' = '" + table.getKafka_topic() + "'," +
                    " 'properties.bootstrap.servers' = '" + KAFKA_URI + "'," +
                    " 'format' = 'json'," +
                    " 'scan.startup.mode' = 'earliest-offset'," +
                    " 'properties.auto.offset.reset' = 'earliest'" +
                    "); ";
            this.tEnv.executeSql(query);
        }
    }

    public ArrayList<YAML_table> getTables() {
        return tables;
    }

    public void executeSql(String s) {
        this.tEnv.executeSql(s);
    }

    public void executeInsertSVQuery(String insertSVQuery) {
        tEnv.createStatementSet().addInsertSql(insertSVQuery).execute();
    }

}
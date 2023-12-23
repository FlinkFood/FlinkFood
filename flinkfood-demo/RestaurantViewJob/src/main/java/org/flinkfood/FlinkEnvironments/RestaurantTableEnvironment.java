package org.flinkfood.FlinkEnvironments;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import org.flinkfood.supportClasses.YAML_table;
import org.flinkfood.supportClasses.YAML_reader;

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
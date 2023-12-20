package org.flinkfood.FlinkEnvironments;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import org.flinkfood.supportClasses.YAML_table;
import org.flinkfood.supportClasses.YAML_reader;

import static org.flinkfood.supportClasses.YAML_reader.readYamlFile;

public class RestaurantTableEnvironment {
    private final StreamTableEnvironment tEnv;
    private static final String KAFKA_URI = "localhost:9092";
    private final ArrayList<YAML_table> tables;

    /**
     * Support class for the creation of the table environment
     * Tables schemas are hardcoded in the class
     */

    public RestaurantTableEnvironment(StreamExecutionEnvironment env, String config_file) throws FileNotFoundException {
        this.tEnv = StreamTableEnvironment.create(env);
        tables = readYamlFile(config_file);

        for (YAML_table table : tables) {
            String query = "CREATE TABLE " + table.getName() + "(" + table.getSchema() + ")" +
                    " WITH (" +
                    " 'connector' = 'kafka'," +
                    " 'topic' = '" + table.getKafka_topic() + "'," +
                    " 'properties.bootstrap.servers' = '" + KAFKA_URI + "'," +
                    " 'format' = 'json'," +
                    " 'scan.startup.mode' = 'earliest-offset'," +
                    " 'properties.auto.offset.reset' = 'earliest'" +
                    "); ";
            System.out.println(query);
            this.tEnv.executeSql(query);
        }
    }

    public ArrayList<YAML_table> getTables() {
        return tables;
    }

    public TableEnvironment gettEnv() {
        return tEnv;
    }

    public void executeSql(String s) {
        tEnv.executeSql(s);
    }

    public StatementSet createStatementSet() {
        return tEnv.createStatementSet();
    }
}
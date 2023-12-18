package org.flinkfood.FlinkEnvironments;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import org.flinkfood.supportClasses.YAML_table;
import org.flinkfood.supportClasses.YAML_reader;

public class RestaurantTableEnvironment {
    private final StreamTableEnvironment tEnv;
    private static final String KAFKA_URI = "localhost:9092";

    /**
     * Support class for the creation of the table environment
     * Tables schemas are hardcoded in the class
     */

    public RestaurantTableEnvironment(StreamExecutionEnvironment env) {
        this.tEnv = StreamTableEnvironment.create(env);
    }

    public void createAllTables() throws FileNotFoundException {
        String query;

        ArrayList<YAML_table> tables = (new YAML_reader("table_config.yml")).readYamlFile();

        for (int i = 0; i < tables.size(); i++) {
            query = "CREATE TABLE " + tables.get(i).getName() + "(" + tables.get(i).getSchema() + ")" +
                    " WITH (" +
                    " 'connector' = 'kafka'," +
                    " 'topic' = '" + tables.get(i).getKafka_topic() + "'," +
                    " 'properties.bootstrap.servers' = '" + KAFKA_URI + "'," +
                    " 'format' = 'json'," +
                    " 'scan.startup.mode' = 'earliest-offset'," +
                    " 'properties.auto.offset.reset' = 'earliest'" +
                    "); ";

            this.tEnv.executeSql(query);
        }

    }

    public TableEnvironment gettEnv() {
        return tEnv;
    }
}
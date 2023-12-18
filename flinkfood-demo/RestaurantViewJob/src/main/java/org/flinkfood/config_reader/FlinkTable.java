package org.flinkfood.config_reader;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/*
 * This class loads the configuration from "table_config"
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlinkTable{

    private String name;
    private String schema;
    private String kafka_topic;

    public FlinkTable()
    {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setKafka_topic(String kafka_topic) {
        this.kafka_topic = kafka_topic;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public String getKafka_topic() {
        return kafka_topic;
    }

}


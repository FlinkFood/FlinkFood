package org.flinkfood.supportClasses;

public class YAML_table {
    
    private final String name;
    private final String schema;
    private final String kafka_topic;

    YAML_table(String name, String schema, String kafka_topic){
        this.name = name;
        this.schema = schema;
        this.kafka_topic = kafka_topic;
    }

    // Constructor with default kafka_topic
    YAML_table(String name, String schema){
        this.name = name;
        this.schema = schema;
        this.kafka_topic = "postgres.public." + name;
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

    @Override
    public String toString() {
        return "YAML_table{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                ", kafka_topic='" + kafka_topic + '\'' +
                '}';
    }
}

package org.flinkfood.serializers;

public class YAML_table {
    
    private String name;
    private String schema;
    private String kafka_topic;

    YAML_table(String name, String schema, String kafka_topic){
        this.name = name;
        this.schema = schema;
        this.kafka_topic = kafka_topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getKafka_topic() {
        return kafka_topic;
    }

    public void setKafka_topic(String kafka_topic) {
        this.kafka_topic = kafka_topic;
    }
    

}

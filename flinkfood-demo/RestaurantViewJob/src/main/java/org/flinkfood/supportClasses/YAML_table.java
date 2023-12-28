package org.flinkfood.supportClasses;

/**
 * The {@code YAML_table} class represents a table configuration parsed from a YAML file. 
 * Each instance contains the table's name, schema, and an optional Kafka topic.
 * 
 * This class is used to create the table environment for the RestaurantView and is used to create the MongoDB collection
 * 
 * This class is extremelly important with reagrds of modularity and readability of the code.
 *
 * he class provides two constructors: one to initialize all fields and another with a default Kafka topic 
 * format if it was not provided earlier. 
 *
 * Table configurations typically consist of a name, a schema definition, and optionally, a Kafka topic 
 * for streaming purposes. The Kafka topic is formatted with a default value if not explicitly provided.
 *
 * The class also provides getter methods for each of its attributes and an overridden {@code toString()} method 
 * to display the object's details in a formatted string. 
 *
 * @author PolimiGiovanniArriciati
 * @version 1.0
 * @see YAML_reader
 */

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

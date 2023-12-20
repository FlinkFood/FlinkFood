package org.flinkfood.supportClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;


public interface YAML_reader {

    // FIXME: where is the file?? (path, it may be setted)
    public static ArrayList<YAML_table> readYamlFile(String filename) throws FileNotFoundException
    {
        Yaml yaml = new Yaml();
        FileInputStream inputStream = new FileInputStream(filename);

        //Reads all yaml as an array of hashmaps
        ArrayList<LinkedHashMap<String, String>> array = yaml.load(inputStream);

        //For each hashmap, mounts one instance of the class
        ArrayList<YAML_table> yamlTables = new ArrayList<>();
        for (LinkedHashMap<String, String> map : array)
        {
            var name = map.get("name");
            var schema = transform(map.get("schema"));
            var kafka_topic = map.get("kafka_topic");
            if (kafka_topic == null) yamlTables.add(new YAML_table(name, schema));
            else yamlTables.add(new YAML_table(name, schema, kafka_topic));
        }
        return yamlTables;
    }

    private static String transform(String schema) {
        return schema
                .replace("serial", "INT")
                .replace("SMALLINT", "INT");
    }
}
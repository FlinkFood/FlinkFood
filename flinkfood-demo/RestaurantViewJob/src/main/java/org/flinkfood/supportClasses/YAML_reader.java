package org.flinkfood.supportClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;


public class YAML_reader {

    private final String filepath;
    ;

    public YAML_reader(String filepath)
    {
        this.filepath = filepath;
    }

    public ArrayList<YAML_table> readYamlFile() throws FileNotFoundException
    {
        Yaml yaml = new Yaml();
        FileInputStream inputStream = new FileInputStream(new File(this.filepath));

        //Reads all yaml as an array of hashmaps
        ArrayList<Map<String, String>> array = yaml.load(inputStream);

        //For each hashmap, mounts one instance of the class
        var yamlTables = new ArrayList<YAML_table>();
        for(Map<String, String> map : array)
        {
            var name = map.get("name");
            var schema = transform(map.get("schema"));
            var kafka_topic = map.get("kafka_topic");
            if (kafka_topic == null)
                yamlTables.add(new YAML_table(name, schema));
            else
                yamlTables.add(new YAML_table(name, schema, kafka_topic));
        }
        return yamlTables;
    }

    private String transform(String schema) {
        return schema
                .replace("serial", "INT")
                .replace("SMALLINT", "INT");
    }
}
package org.flinkfood.serializers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;


public class YAML_reader {

    private String filepath;

    public ArrayList<YAML_table> readYamlFile() throws FileNotFoundException
    {
        Yaml yaml = new Yaml();
        FileInputStream inputStream = new FileInputStream(new File(this.filepath));

        //Reads all yaml as an array of hashmaps
        ArrayList array = yaml.load(inputStream);
        
        //For each hashmap, mounts one instance of the class
        ArrayList<YAML_table> objects = new ArrayList<YAML_table>();
        for(int i = 0; i < array.size(); i++)
        {   
            LinkedHashMap temp = (LinkedHashMap) array.get(i);
            YAML_table element = new YAML_table((String) temp.get("name"), 
                                                (String) temp.get("schema"),
                                                (String) temp.get("kafka_topic"));
            objects.add(element);
        }        

        return objects;
    }

    public YAML_reader(String filepath)
    {
        this.filepath = filepath;
    }
}
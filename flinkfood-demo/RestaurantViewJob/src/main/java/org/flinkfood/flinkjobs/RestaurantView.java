// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.flinkfood.ArrayAggr;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.config_reader.FlinkTable;

import com.twitter.chill.Base64.InputStream;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.flink.table.api.Expressions.*;


//import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

// Class declaration for the Flink job
public class RestaurantView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_view";

    private static final List<String> tables = List.of("restaurant_info", "dish", "restaurant_service", "restaurant_address", "restaurant_review", "review_dish");
    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RestaurantTableEnvironment rEnv = new RestaurantTableEnvironment(env);
        
        rEnv.createAllTables();
        //TODO: get the tables from the config file
        /*rEnv.createRestaurantInfoTable();
        rEnv.createDishesTable();
        rEnv.createReviewDishTable();
        rEnv.createRestaurantServicesTable();
        rEnv.createRestaurantAddressTable();
        rEnv.createRestaurantReviewsTable();
        */

        Schema.Builder schemaBuilder = Schema.newBuilder();

        //TODO: for the single view create a schema with ARRAY<ROW<...>> for each table that contains the restaurant_id column
        for (String table : tables) {
            if (Arrays.asList(rEnv.gettEnv().listTables()).contains(table)) { // check if the table is present
                ResolvedSchema resolvedSchema = rEnv.gettEnv().from(table).getResolvedSchema();
                // if the table contains the restaurant_id column, add it to the view
                if (resolvedSchema.getColumnNames().stream()
                        .anyMatch(s -> s.equals("restaurant_id"))) {
                    String aggr_table_schema = "ARRAY<ROW<" + resolvedSchema.getColumnNames().stream()
                            .map(s -> s + " " + resolvedSchema.getColumn(s).get().getName())
                            .reduce((s1, s2) -> s1 + "," + s2).get() + ">>";

                    schemaBuilder.column(table, aggr_table_schema);
                }
            }
        }

        TableDescriptor tableDescriptor = TableDescriptor.forConnector("mongodb")
                .schema(schemaBuilder.build()).build(); //:pinched-fingers:

        // Tables cannot be saved locally without a  ManagedTableFactory
        // -> rn they can go just in a sink (MongoDB for example) or be printed out.

        // rEnv.gettEnv().createTable("restaurant_view_", tableDescriptor);

        // declaration of the table view to be sinked. I want to wwitch to use the one above in the future
        rEnv.gettEnv()
                .executeSql(
                "CREATE TABLE restaurant_view "+
                        "(restaurant_id INT, "+
                        "dishes " +
                        "ARRAY<ROW<" +
                        "id BIGINT," +
                        "restaurant_id BIGINT," +
                        "dish_name STRING," +
                        "price INT," +
                        "currency STRING," +
                        "category STRING," +
                        "description STRING>>," +
                        "PRIMARY KEY (restaurant_id) NOT ENFORCED) " +
                        "WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017'," +
                        "'database' = 'flinkfood'," +
                        "'collection' = 'restaurants_view')");


        // this command does the registration in Table API
        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");

        List<Table> aggregatedTables = new ArrayList<>();
        for(String table : tables) {
            var resolvedSchema = rEnv.gettEnv().from(table).getResolvedSchema();
            if (resolvedSchema.getColumnNames().stream()
                    .anyMatch(s -> s.equals("restaurant_id"))) {

                aggregatedTables.add(
                        rEnv.gettEnv()
                                .from(table)
                                .groupBy($("restaurant_id"))
                                .aggregate(call(ArrayAggr.class))
                                .select($("*")));
            }
        }

        Table tempTable = aggregatedTables.get(0);
        for (int i = 1; i < aggregatedTables.size(); i++) {
            tempTable = tempTable.join(aggregatedTables.get(i)).where($("restaurant_id").isEqual($("restaurant_id"))).select($("*"));
        }

        tempTable.execute().print();


        env.execute("RestaurantView");
    }

}
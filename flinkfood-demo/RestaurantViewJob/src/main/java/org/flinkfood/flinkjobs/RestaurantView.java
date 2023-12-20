// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.supportClasses.YAML_table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// Class declaration for the Flink job
public class RestaurantView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_view";

    @Deprecated
    private static final List<String> tables = List.of("restaurant_info", "dish", "restaurant_service", "restaurant_address", "restaurant_review", "review_dish");
    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RestaurantTableEnvironment rEnv = new RestaurantTableEnvironment(env);
        
        List<YAML_table> tables_ = rEnv.createAllTables();
        var tEnv = rEnv.gettEnv();

        // this command does the registration in Table API
        tEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");
        createSV(tables_, "restaurant_id", tEnv);
        env.execute("RestaurantView");
    }

    private static void createSV(List<YAML_table> tables_, String viewKey, TableEnvironment tEnv) {
        // remove tables that do not contain the viewKey
        List<YAML_table> tables = tables_.stream().filter(t -> t.getSchema().contains(viewKey)).collect(Collectors.toList());

        if (tables.isEmpty()) {
            System.err.println("No table contains the view key");
            return;
        }

        // create view table
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append("view (");
        sb.append(viewKey);
        sb.append(" INT primary key not enforced, ");
        for (YAML_table table : tables) {
            sb.append(table.getName());
            sb.append(" ");
            sb.append("ARRAY<ROW<");
            sb.append(table.getSchema());
            sb.append(">>,\n");
        }
        sb.delete(sb.length() - 2, sb.length() - 1); //Remove last comma and newline
        sb.append(") WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017'," +
                "'database' = 'flinkfood'," +
                "'collection' = '" + viewKey.replace("_id","") + "_view' )");
        tEnv.executeSql(sb.toString());

        // insertion query (not working)
        sb = new StringBuilder();
        sb.append("INSERT INTO view ");
        sb.append("SELECT ");
        sb.append(tables.get(0).getName()); //reference the first table for the index key: if in the first table there is not the restaurant, then it will not be in the view!
        sb.append(".");
        sb.append(viewKey);
        sb.append(",\n");
        for (YAML_table table : tables) {
            sb.append("ARRAY_AGGR(ROW(");
            sb.append(clearTypesAndAddTableReference(table));
            sb.append("))\n,");
        }
        sb.deleteCharAt(sb.length() - 1); //Remove last comma
        sb.append(" FROM ");
        sb.append(tables.get(0).getName());
        for (int i = 1; i < tables.size(); i++) {
            sb.append("\nLEFT JOIN ");
            sb.append(tables.get(i).getName());
            sb.append(" ON ");
            sb.append(tables.get(0).getName());
            sb.append(".");
            sb.append(viewKey);
            sb.append(" = ");
            sb.append(tables.get(i).getName());
            sb.append(".");
            sb.append(viewKey);
            sb.append("\n");
        }
        sb.append("GROUP BY ");
        sb.append(tables.get(0).getName());
        sb.append(".");
        sb.append(viewKey);
        sb.append(";");
        System.out.println(sb);
        tEnv.createStatementSet().addInsertSql(sb.toString()).execute();
    }

    private static String clearTypesAndAddTableReference(YAML_table table) {
        return Arrays.stream(table.getSchema().trim().split("\\s*,\\s*")) // Split by comma and any whitespace
                    .map(s -> s.split("\\s+")[0]) // Get the first word of each part
                    .map(s -> table.getName() + "." + s) // Add table reference
                    .collect(Collectors.joining(", "));
        }

    }
// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.supportClasses.YAML_table;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// Class declaration for the Flink job
public class RestaurantView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String SINK_DB = "flinkfood";
    public static final String VIEW_KEY = "restaurant_id";
    public static final String VIEW_NAME = "restaurant_view";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception, FileNotFoundException {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RestaurantTableEnvironment rEnv = new RestaurantTableEnvironment(env, "table_config.yaml");
        // this command does the registration in Table API
        rEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.supportClasses.ArrayAggr'");

        createSV(VIEW_KEY, rEnv, VIEW_NAME);
        env.execute("RestaurantView");
    }

    private static void createSV(String viewKey, RestaurantTableEnvironment rEnv, String view_name) {
        List<YAML_table> tables = getViewTables(rEnv.getTables(), viewKey);
        rEnv.executeSql(createSVTable(viewKey, tables, view_name));
        var insertSVQuery = createInsertSVQuery(viewKey, tables);
        executeInsertSVQuery(rEnv, insertSVQuery);

    }

    // remove tables that do not contain the viewKey
    private static List<YAML_table> getViewTables(List<YAML_table> tables_, String viewKey) {
        List<YAML_table> tables = new ArrayList<>();
        for (YAML_table t : tables_) {
            if (t.getSchema().contains(viewKey)) {
                tables.add(t);
            }
        }
        if (tables.isEmpty()) System.err.println("No table contains the view key");
        if (tables.size() == 1) {
            System.err.println("Only one table contains the view key, at least two are required to create a view");
            throw new IllegalArgumentException("Only one table contains the view key, at least two are required to create a view");
        }
        return tables;
    }

    private static String createSVTable(String viewKey, List<YAML_table> tables, String view_name) {
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
        sb.append(") WITH (" +
                "'connector' =          'mongodb'," +
                "'uri' = '" +           MONGODB_URI + "'," +
                "'database' = '" +      SINK_DB     + "'," +
                "'collection' = '" +    view_name   + "')");
        return sb.toString();
    }

    private static String createInsertSVQuery(String viewKey, List<YAML_table> tables) {
        if (tables.size() < 2) throw new IllegalArgumentException("At least two tables are required to create a view");

        var sb = new StringBuilder();
        sb.append("INSERT INTO view ");
        sb.append("SELECT ");
        sb.append(tables.get(0).getName()); //reference the first table for the index key: if in the first table there is not the restaurant, then it will not be in the view!
        sb.append(".");
        sb.append(viewKey);
        sb.append(",\n");
        for (YAML_table table : tables) {
            sb.append("ARRAY_AGGR( ROW(");
            sb.append(getFieldNamesWithTableReference(table));
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
        return sb.toString();
    }

    private static void executeInsertSVQuery(RestaurantTableEnvironment rEnv, String insertSVQuery) {
        rEnv.createStatementSet().addInsertSql(insertSVQuery).execute();
    }

    private static String getFieldNamesWithTableReference(YAML_table table) {
        return Arrays.stream(table.getSchema().trim().split("\\s*,\\s*")) // Split by comma and any whitespace
                    .map(s -> s.split("\\s+")[0])                         // Get the first word of each line (the field name)
                    .map(s -> table.getName() + "." + s)                         // Add table reference
                    .collect(Collectors.joining(", "));
        }
    }
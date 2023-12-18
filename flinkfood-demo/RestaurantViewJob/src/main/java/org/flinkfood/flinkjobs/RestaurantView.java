// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.supportClasses.YAML_table;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        // createSV(tables_, "restaurant_id", tEnv);
        tEnv.executeSql("CREATE TABLE view (restaurant_id INT primary key not enforced,"+
                " restaurant_service ARRAY<ROW<restaurant_id INT , \n" +
                "take_away BOOLEAN, \n" +
                "delivery BOOLEAN, \n" +
                "dine_in BOOLEAN, \n" +
                "parking_lots INT, \n" +
                "accessible BOOLEAN, \n" +
                "children_area BOOLEAN, \n" +
                "children_food BOOLEAN\n" +
                ">>," +
                "restaurant_address ARRAY<ROW<restaurant_id INT , \n" +
                "street VARCHAR(255), \n" +
                "address_number VARCHAR(10), \n" +
                "zip_code INT, \n" +
                "city VARCHAR(255), \n" +
                "province VARCHAR(2), \n" +
                "country VARCHAR(255)\n" +
                ">>," +
                "restaurant_review ARRAY<ROW<id INT , \n" +
                "restaurant_id INT, \n" +
                "customer_id INT, \n" +
                "rating SMALLINT, \n" +
                "commentary VARCHAR(255)\n" +
                ">>," +
                "dish ARRAY<ROW<id INT , \n" +
                "restaurant_id INT, \n" +
                "name VARCHAR(255), \n" +
                "price SMALLINT, \n" +
                "currency VARCHAR(3), \n" +
                "category VARCHAR(255), \n" +
                "description VARCHAR(255)\n" +
                ">>)" +
                " WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017','database' = 'flinkfood','collection' = 'restaurants_view')");

       tEnv.createStatementSet().addInsertSql("INSERT INTO view SELECT restaurant_id,\n" +
               "(SELECT ARRAY_AGGR(ROW(restaurant_id,take_away,delivery,dine_in,parking_lots,accessible,children_area,children_food)) FROM restaurant_service GROUP BY restaurant_id),\n" +
               "(SELECT ARRAY_AGGR(ROW(restaurant_id,street,address_number,zip_code,city,province,country)) FROM restaurant_address GROUP BY restaurant_id),\n" +
               "(SELECT ARRAY_AGGR(ROW(id,restaurant_id,customer_id,rating,commentary)) FROM restaurant_review GROUP BY restaurant_id),\n" +
               "(SELECT ARRAY_AGGR(ROW(id,restaurant_id,name,price,currency,category,description)) FROM dish GROUP BY restaurant_id) FROM restaurant_service")
                       .execute()
                               .print();

        env.execute("RestaurantView");
        /*
         doesn't work but i got a very cool error message:
           FlinkLogicalSink(table=[default_catalog.default_database.view], fields=[restaurant_id, $f0, $f00, $f01, $f02])
           +- FlinkLogicalJoin(condition=[true], joinType=[left])
           :- FlinkLogicalJoin(condition=[true], joinType=[left])
           :  :- FlinkLogicalJoin(condition=[true], joinType=[left])
           :  :  :- FlinkLogicalJoin(condition=[true], joinType=[left])
           :  :  :  :- FlinkLogicalCalc(select=[restaurant_id])
           :  :  :  :  +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_service]], fields=[restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food])
           :  :  :  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
           :  :  :     +- FlinkLogicalCalc(select=[EXPR$0])
           :  :  :        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
           :  :  :           +- FlinkLogicalCalc(select=[restaurant_id, ROW(restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) AS $f1])
           :  :  :              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_service]], fields=[restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food])
           :  :  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
           :  :     +- FlinkLogicalCalc(select=[EXPR$0])
           :  :        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
           :  :           +- FlinkLogicalCalc(select=[restaurant_id, ROW(restaurant_id, street, address_number, zip_code, city, province, country) AS $f1])
           :  :              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_address]], fields=[restaurant_id, street, address_number, zip_code, city, province, country])
           :  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
           :     +- FlinkLogicalCalc(select=[EXPR$0])
           :        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
           :           +- FlinkLogicalCalc(select=[restaurant_id, ROW(id, restaurant_id, customer_id, rating, commentary) AS $f1])
           :              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_review]], fields=[id, restaurant_id, customer_id, rating, commentary])
           +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
              +- FlinkLogicalCalc(select=[EXPR$0])
                 +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
                    +- FlinkLogicalCalc(select=[restaurant_id, ROW(id, restaurant_id, name, price, currency, category, description) AS $f1])
                       +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, dish]], fields=[id, restaurant_id, name, price, currency, category, description])

        SINGLE_VALUE aggregate function doesn't support type 'ARRAY'.
         */
    }

    private static void createSV(List<YAML_table> tables, String viewKey, TableEnvironment tEnv) {
        // remove tables that do not contain the viewKey
        tables = tables.stream().filter(t -> t.getSchema().contains(viewKey)).collect(Collectors.toList());

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
                "'collection' = 'restaurants_view')");
        tEnv.executeSql(sb.toString());

        // insertion query (not working)
        sb = new StringBuilder();
        sb.append("INSERT INTO view ");
        sb.append("SELECT ");
        sb.append(viewKey);
        sb.append(",");
        for (YAML_table table : tables) {
            sb.append("\n(SELECT ");
            sb.append("ARRAY_AGGR(ROW(");
            sb.append(clearTypes(table.getSchema()));
            sb.append(")) ");
            sb.append("FROM ");
            sb.append(table.getName());
            sb.append(" GROUP BY ");
            sb.append(viewKey);
            sb.append("),");
        }
        sb.deleteCharAt(sb.length() - 1); //Remove last comma
        sb.append(" FROM ");
        sb.append(tables.get(0).getName());
        tEnv.createStatementSet().addInsertSql(sb.toString());
    }

    private static String clearTypes(String schema) {
        return Arrays.stream(schema.trim().split("\\s*,\\s*")) // Split by comma and any whitespace
                    .map(s -> s.split("\\s+")[0]) // Get the first word of each part
                    .collect(Collectors.joining(","));
        }

    }
// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;

import java.util.List;

import static org.apache.flink.table.api.Expressions.*;

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
        rEnv.createRestaurantInfoTable();
        rEnv.createDishesTable();
        rEnv.createReviewDishTable();
        rEnv.createRestaurantServicesTable();
        rEnv.createRestaurantAddressTable();
        rEnv.createRestaurantReviewsTable();

//        MongoSink<Row> sink = MongoSink.<Row>builder()
//                .setUri(MONGODB_URI)
//                .setDatabase(SINK_DB)
//                .setCollection(SINK_DB_TABLE)
//                .setBatchSize(1000)
//                .setBatchIntervalMs(1000)
//                .setMaxRetries(3)
//                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
//                .setSerializationSchema(new RestaurantRowToBsonDocument())
//                .build();

        Schema.Builder schemaBuilder = Schema.newBuilder();

        //TODO: for the single view create a schema with ARRAY<ROW<...>> for each table that contains the restaurant_id column
        for (String table : tables) {
            ResolvedSchema resolvedSchema = rEnv.gettEnv().from(table).getResolvedSchema();
            if (resolvedSchema.getColumnNames().stream()
                    .anyMatch(s -> s.equals("restaurant_id"))) {
                String aggr_table_schema = "ARRAY<ROW<" + resolvedSchema.getColumnNames().stream()
                        .map(s -> s + " " + resolvedSchema.getColumn(s).get().getName()) //.getDataType().getLogicalType().getTypeRoot().name())
                        .reduce((s1, s2) -> s1 + "," + s2).get() + ">>";
            } else {
                // table is not in the view
            }
//            schemaBuilder.column(resolvedSchema.getColumnNames().stream()
//                    .map(s -> s + " " + resolvedSchema.getColumn(s).get().getName()) //.getDataType().getLogicalType().getTypeRoot().name())
//                    .reduce((s1, s2) -> s1 + "," + s2).get()
//            )
        }

        TableDescriptor tableDescriptor = TableDescriptor.forConnector("mongodb")
                .schema(schemaBuilder.build()).build(); //:pinched-fingers:


        // The idea is to use the schema to have the types in the view
//            rEnv.gettEnv()
//                .from("dish")
//                .getResolvedSchema();
//        -->  (    `id` BIGINT,
//                  `restaurant_id` INT,
//                  `name` STRING,
//                  `price` SMALLINT,
//                  `currency` STRING,
//                  `category` STRING,
//                  `description` STRING
//                  )


// Tables cannot be saved locally without a  ManagedTableFactory
// -> rn they can go just in a sink (MongoDB for example) or be printed out.

        rEnv.gettEnv().createTable("restaurant_view_", tableDescriptor);

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


        // this command submits the function to the SQL engine
        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");


        // aggregation is created with the insert statement, adding the data into the restaurant_view table.
        // maybe it's possible to use just an intermediary view.
        var stmtSet = rEnv.gettEnv().createStatementSet();
        stmtSet.addInsertSql(
                "INSERT INTO restaurant_view " +
                "SELECT restaurant_id, " +
                    // here would be nice to have a modular way to add the tables to the view
                    "ARRAY_AGGR(ROW(id, restaurant_id, name, price, currency, category, description)) " +
                    "FROM dish " +
                    "GROUP BY restaurant_id ")
                .execute();

        env.execute("RestaurantView");
    }

}
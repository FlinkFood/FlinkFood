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

    private static final List<String> tables = List.of("restaurant_info", "dishes", "restaurant_services", "restaurant_address", "restaurant_reviews", "review_dish");
    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RestaurantTableEnvironment rEnv = new RestaurantTableEnvironment(env);
        rEnv.createRestaurantInfoTable();
        rEnv.createDishesTable();
//        rEnv.createRestaurantServicesTable();
//        rEnv.createRestaurantAddressTable();
//        rEnv.createRestaurantReviewsTable();
        rEnv.createReviewDishTable();

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

        //for the single view create a schema with ARRAY<ROW<...>> for each table that contains the restaurant_id column
        for (String table : tables) {
            ResolvedSchema resolvedSchema = rEnv.gettEnv().from(table).getResolvedSchema();
            if (resolvedSchema.getColumnNames().stream()
                    .anyMatch(s -> s.equals("restaurant_id"))) {
                String aggr_table_schema = "ARRAY<ROW<" + resolvedSchema.getColumnNames().stream()
                        .map(s -> s + " " + resolvedSchema.getColumn(s).get().getName()) //.getDataType().getLogicalType().getTypeRoot().name())
                        .reduce((s1, s2) -> s1 + "," + s2).get() + ">>";
            }
            schemaBuilder.column(resolvedSchema.getColumnNames().stream()
                    .map(s -> s + " " + resolvedSchema.getColumn(s).get().getName()) //.getDataType().getLogicalType().getTypeRoot().name())
                    .reduce((s1, s2) -> s1 + "," + s2).get()
            )
        }
        TableDescriptor tableDescriptor = new TableDescriptor("restaurant_view", schemaBuilder.build());


            rEnv.gettEnv()
                .from("dish")
                .getResolvedSchema();
//        -->  (    `id` BIGINT,
//                  `restaurant_id` INT,
//                  `name` STRING,
//                  `price` SMALLINT,
//                  `currency` STRING,
//                  `category` STRING,
//                  `description` STRING
//                  )

        // TODO: have a ManagedTableFactory to save tables in flink! -> rn they can go just in a sink
        rEnv.gettEnv()
                .createTable("restaurant_view").executeSql(
                "CREATE TABLE restaurant_view "+
                        "(restaurant_id INT PRIMARY KEY NOT ENFORCED, "+
                        "dishes " +
                        "ARRAY<ROW<" +
                        "id BIGINT," +
                        "restaurant_id BIGINT," +
                        "dish_name STRING," +
                        "price INT," +
                        "currency STRING," +
                        "category STRING," +
                        "description STRING>>)" +
                        "WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017'," +
                        "'database' = 'flinkfood'," +
                        "'collection' = 'restaurants_view')");

        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");

        var stmtSet = rEnv.gettEnv().createStatementSet();

        stmtSet.addInsertSql(
                "INSERT INTO restaurant_view " +
                "SELECT restaurant_id, " +
                    "ARRAY_AGGR(ROW(id, restaurant_id, name, price, currency, category, description)) " +
                    "FROM dish " +
                    "GROUP BY restaurant_id ");

        stmtSet.execute();

        //Execute the Flink job with the given name
        env.execute("RestaurantView");

        /*
        Exception in thread "main" java.lang.IllegalStateException: No operators defined in streaming topology. Cannot execute.
        at org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.getStreamGraphGenerator(StreamExecutionEnvironment.java:2322)
        at org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.getStreamGraph(StreamExecutionEnvironment.java:2289)
        at org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.getStreamGraph(StreamExecutionEnvironment.java:2280)
        at org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.getStreamGraph(StreamExecutionEnvironment.java:2266)
        at org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.execute(StreamExecutionEnvironment.java:2093)
        at org.flinkfood.flinkjobs.RestaurantView.main(RestaurantView.java:98)*/
    }

}
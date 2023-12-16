// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.runtime.functions.aggregate.JsonArrayAggFunction;
import org.apache.flink.types.Row;
import org.flinkfood.ArrayAggr;
import org.flinkfood.FlinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.serializers.RestaurantRowToBsonDocument;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.streaming.api.datastream.DataStream;


import static org.apache.flink.table.api.Expressions.*;

// Class declaration for the Flink job
public class RestaurantView {

    private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_view";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RestaurantTableEnvironment rEnv = new RestaurantTableEnvironment(env);
        rEnv.createRestaurantInfoTable();
//        rEnv.createRestaurantServicesTable();
//        rEnv.createRestaurantAddressTable();
//        rEnv.createRestaurantReviewsTable();
        rEnv.createDishesTable();
        rEnv.createReviewDishTable();

        MongoSink<Row> sink = MongoSink.<Row>builder()
                .setUri(MONGODB_URI)
                .setDatabase(SINK_DB)
                .setCollection(SINK_DB_TABLE)
                .setBatchSize(1000)
                .setBatchIntervalMs(1000)
                .setMaxRetries(3)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setSerializationSchema(new RestaurantRowToBsonDocument())
                .build();

//        System.out.println(
//        rEnv.gettEnv()
//                .from("dish")
//                .getResolvedSchema());
//        -->  (    `id` BIGINT,
//                  `restaurant_id` INT,
//                  `name` STRING,
//                  `price` SMALLINT,
//                  `currency` STRING,
//                  `category` STRING,
//                  `description` STRING
//                  )
//        rEnv.gettEnv().executeSql("CREATE TABLE restaurant_view"+
//                                "( restaurant_id INT,"+
//                                " dishes ARRAY<ROW<id BIGINT, restaurant_id INT, name STRING, price SMALLINT, currency STRING, category STRING, description STRING>>)");

        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");

        rEnv.gettEnv().executeSql("CREATE TABLE restaurant_view"+
                                            "( restaurant_id INT,"+
                                            " dishes ARRAY<ROW<id BIGINT, restaurant_id BIGINT, name STRING, price SMALLINT, currency STRING, category STRING, description STRING>>)" +
                                            " WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017', 'database' = 'flinkfood', 'collection' = 'restaurants_view')"
                                            );
        //TODO: have a ManagedTableFactory to save tables in flink!

        Table resultTable3 = rEnv.gettEnv()
                .sqlQuery("SELECT restaurant_id," +
                        "ARRAY_AGGR(ROW(id, restaurant_id, name, price, currency, category, description)) AS restaurant_view FROM dish GROUP BY restaurant_id");
//        rEnv.gettEnv()
//                        .from("dish")
//                        .groupBy($("restaurant_id"))
//                        .aggregate(call(ArrayAggr.class))
//                        .select($("*"))
//                                .execute()
//                                        .print();
        
//        System.out.println(
//        rEnv.gettEnv()
//                .from("dish")
//                .groupBy($("restaurant_id"))
//                .flatAggregate(call(ArrayAggr.class))
//                .select($("*"))
//                .getResolvedSchema());




        DataStream<Row> resultStream = rEnv.toDataStream(resultTable3);
        resultStream.sinkTo(sink);
        

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

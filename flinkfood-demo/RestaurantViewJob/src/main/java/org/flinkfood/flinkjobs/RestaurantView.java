// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.flinkfood.flinkEnvironments.RestaurantTableEnvironment;
import org.flinkfood.serializers.RestaurantRowToBsonDocument;

import java.util.Optional;

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
        rEnv.createRestaurantServicesTable();
        rEnv.createRestaurantAddressTable();
        rEnv.createRestaurantReviewsTable();
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
                .setSerializationSchema(new RestaurantRowToBsonDocument( ))
                .build();

        //read from file the query TODO: replace with a proper input query
        Optional<String> query = ReadFile.read("./.config/query.sql");
        Table simpleUnifiedTable = rEnv.createSimpleUnifiedRestaurantView(query);
        DataStream<Row> resultStream = rEnv.toDataStream(simpleUnifiedTable);
        resultStream.sinkTo(sink);

        //Execute the Flink job with the given name
        env.execute("RestaurantTableView");
    }
}


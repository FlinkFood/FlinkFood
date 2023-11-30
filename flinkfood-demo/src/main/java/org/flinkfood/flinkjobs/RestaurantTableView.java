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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

// Class declaration for the Flink job
public class RestaurantTableView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
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
class ReadFile {
    public static Optional<String> read(String fileWithPath) {
        StringBuilder content = new StringBuilder();
        try {
            File myObj = new File(fileWithPath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content.append(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            // TODO: replace with more robust error handling
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(content.toString());
    }
}
// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.flinkfood.FlinkEnvironments.CustomerTableEnvironment;

// Class declaration for the Flink job
public class CustomerViewJob {

    private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "customer_view";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CustomerTableEnvironment rEnv = new CustomerTableEnvironment(env);
        rEnv.createCustomerTable();
        rEnv.createCustomer_addressTable();
        rEnv.createSimpleUnifiedRestaurantView();

        /*MongoSink<Row> sink = MongoSink.<Row>builder()
                .setUri(MONGODB_URI)
                .setDatabase(SINK_DB)
                .setCollection(SINK_DB_TABLE)
                .setBatchSize(1000)
                .setBatchIntervalMs(1000)
                .setMaxRetries(3)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setSerializationSchema(new RestaurantRowToBsonDocument())
                .build();
        */

        Table simpleUnifiedTable = rEnv.createSimpleUnifiedRestaurantView();
        DataStream<Row> resultStream = rEnv.toDataStream(simpleUnifiedTable);
        //resultStream.sinkTo(sink);
        resultStream.print();

        //Execute the Flink job with the given name
        env.execute("CystinerViewJob");
    }
}
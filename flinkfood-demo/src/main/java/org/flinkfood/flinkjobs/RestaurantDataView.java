// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.flinkfood.schemas.restaurant.RestaurantAddress;
import org.flinkfood.schemas.restaurant.RestaurantInfo;
import org.flinkfood.schemas.restaurant.RestaurantView;
import org.apache.flink.streaming.api.windowing.time.Time;

import javax.xml.crypto.Data;
import java.io.IOException;


// Class declaration for the Flink job
public class RestaurantDataView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurant_aggregated_view";
    private static final String KAFKA_URI = "localhost:9092";

    // Main method where the Flink job is defined
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //TODO: import also the timestamps for the messages, those will be needed for versioning.
        //TODO: value having a map of kafka sources for better readability
        KafkaSource<RestaurantInfo> RestaurantInfoSource =
                KafkaSource.<RestaurantInfo>builder()
                        .setBootstrapServers(KAFKA_URI)
                        .setTopics("postgres.public.restaurant_info")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new RestaurantInfo.Deserializer())
                        .build();
        KafkaSource<RestaurantAddress> RestaurantAddressSource =
                KafkaSource.<RestaurantAddress>builder()
                        .setBootstrapServers(KAFKA_URI)
                        .setTopics("postgres.public.restaurant_address")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new RestaurantAddress.Deserializer())
                        .build();


        MongoSink<RestaurantView> sink = MongoSink.<RestaurantView>builder()
                .setUri(MONGODB_URI)
                .setDatabase(SINK_DB)
                .setCollection(SINK_DB_TABLE)
                .setBatchSize(1000)
                .setBatchIntervalMs(1000)
                .setMaxRetries(3)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setSerializationSchema(new RestaurantView.Serializer())
                .build();

        // Creates a Flink data stream for the restaurants
        DataStream<RestaurantInfo> restaurantInfoDataStream = env
                .fromSource(RestaurantInfoSource, WatermarkStrategy.noWatermarks(), "Kafka Restaurant Info Source")
                .setParallelism(5); // 5 is the number of parallel jobs

        DataStream<RestaurantAddress> restaurantAddressDataStream = env
                .fromSource(RestaurantAddressSource, WatermarkStrategy.noWatermarks(), "Kafka Restaurant Address Source")
                .setParallelism(5); // 5 is the number of parallel jobs
        /* not used in testing
        DataStream<RestaurantService> restaurantServiceDataStream = env
                .fromSource(RestaurantServiceSource, WatermarkStrategy.noWatermarks(), "Kafka Restaurant Service Source")
                .setParallelism(5);
        DataStream<Order> orderDataStream = env
                .fromSource(OrderSource, WatermarkStrategy.noWatermarks(), "Kafka Order Source")
                .setParallelism(5);
         */


  /*      // actual job: aggregation
        DataStreamSink<RestaurantView> restaurantViewDataStream =
        restaurantInfoDataStream.map(restaurantInfo -> new RestaurantView().with(restaurantInfo))
                .join(restaurantAddressDataStream)
                .where(RestaurantView::getRestaurantId)
                .equalTo(RestaurantAddress::getRestaurantId)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .apply(JoinFunction<RestaurantView, RestaurantAddress> (r))
                .sinkTo(sink);
*/
        //restaurantViewDataStream.print();
        //restaurantViewDataStream.sinkTo(sink);
        //restaurantViewDataStream.setParallelism(3);

        //Execute the Flink job with the given name
        env.execute("RestaurantDataView");
    }


}
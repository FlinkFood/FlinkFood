// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

// Importing necessary Flink libraries and external dependencies

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.mongodb.sink.MongoSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.table.runtime.util.StreamRecordCollector;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.flinkfood.schemas.order.Order;
import org.flinkfood.schemas.restaurant.RestaurantInfo;
import org.flinkfood.schemas.restaurant.RestaurantService;
import org.flinkfood.schemas.restaurant.RestaurantView;
import org.flinkfood.serializers.RestaurantRowToBsonDocument;
import org.apache.flink.streaming.api.windowing.time.Time;



// Class declaration for the Flink job
public class RestaurantDataView {

    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String SINK_DB = "flinkfood";
    private static final String SINK_DB_TABLE = "restaurants_view";
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
                        //.setGroupId("restaurant_info") why is this needed?
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new RestaurantInfo.Deserializer())
                        .build();
        KafkaSource<Order> OrderSource =
                KafkaSource.<Order>builder()
                        .setBootstrapServers(KAFKA_URI)
                        .setTopics("postgres.public.restaurant_order")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new Order.Deserializer())
                        .build();
        KafkaSource<RestaurantService> RestaurantServiceSource =
                KafkaSource.<RestaurantService>builder()
                        .setBootstrapServers(KAFKA_URI)
                        .setTopics("postgres.public.restaurant_service")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new RestaurantService.Deserializer())
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

        DataStream<RestaurantService> restaurantServiceDataStream = env
                .fromSource(RestaurantServiceSource, WatermarkStrategy.noWatermarks(), "Kafka Restaurant Service Source")
                .setParallelism(5);

        DataStream<Order> orderDataStream = env
                .fromSource(OrderSource, WatermarkStrategy.noWatermarks(), "Kafka Order Source")
                .setParallelism(5);

        //actual job: aggregation
        restaurantInfoDataStream.coGroup(restaurantServiceDataStream)
                .where(RestaurantInfo::getId)
                .equalTo(RestaurantService::getRestaurant_id)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .apply((CoGroupFunction<RestaurantInfo, RestaurantService, RestaurantView>) (first, second, out) ->
                        first.forEach(restInfo ->
                            second.forEach(rest_service ->
                                    out.collect(new RestaurantView().with(restInfo).with(rest_service)))))
                .coGroup(orderDataStream)
                .where(RestaurantView::getRestaurant_id)
                .equalTo(Order::getRestaurant_id)
                .window(TumblingEventTimeWindows.of(Time.seconds(5))) // I don't know what this is for
                .apply((CoGroupFunction<RestaurantView, Order, RestaurantView>) (first, second, out) ->
                        first.forEach(restaurantView ->
                                second.forEach(order ->
                                    out.collect(restaurantView.with(order)))))
                .addSink((SinkFunction<RestaurantView>) sink);



        //Execute the Flink job with the given name
        env.execute("RestaurantDataView");
    }


}
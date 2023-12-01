package org.flinkfood._helper;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.flinkfood.schemas.restaurant.RestaurantAddress;
import org.flinkfood.schemas.restaurant.RestaurantView;

public class CoFlatMapFunctionImpl_ implements CoFlatMapFunction<RestaurantView, RestaurantAddress, RestaurantView> {

    private DataStream.Collector<RestaurantView> out;
    public CoFlatMapFunctionImpl_() {}

    @Override
    public void flatMap1(RestaurantView value, Collector<RestaurantView> out) throws Exception {
        out.collect(value);
    }

    @Override
    public void flatMap2(RestaurantAddress value, Collector<RestaurantView> out) throws Exception {
        out.collect(new RestaurantView().with(value));
    }
}

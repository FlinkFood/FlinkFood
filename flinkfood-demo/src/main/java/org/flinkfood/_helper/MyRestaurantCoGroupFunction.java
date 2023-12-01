package org.flinkfood._helper;

import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.util.Collector;
import org.flinkfood.schemas.restaurant.*;

public class MyRestaurantCoGroupFunction
        implements CoGroupFunction<RestaurantView, RestaurantAddress,  RestaurantView> {

    public MyRestaurantCoGroupFunction() {
    }
    @Override
    public void coGroup(Iterable<RestaurantView> second,
                        Iterable<RestaurantAddress> first,
                        Collector<RestaurantView> out) {
        RestaurantAddress RestaurantAddress = first.iterator().next();
        RestaurantView restaurantView = second.iterator().next();
        // resturant_id are the same because of the way the function in applyed
        restaurantView.with(RestaurantAddress);
        out.collect(restaurantView);
        if (! first.iterator().hasNext() && ! second.iterator().hasNext()) {
            System.out.println("All data processed");
            out.close();
        }
    }

/*
    @Override
    public void coGroup(Iterable<RestaurantInfo> first, Iterable<RestaurantView> second, Collector<RestaurantView> out) throws Exception {
        RestaurantInfo restaurantInfo = first.iterator().next();
        RestaurantView restaurantView = second.iterator().next();
        // resturant_id are the same because of the way the function in applyed
        restaurantView.with(restaurantInfo);
        out.collect(restaurantView);
    }
*/

}

package org.flinkfood._helper;

import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.util.Collector;
import org.flinkfood.schemas.restaurant.RestaurantAddress;
import org.flinkfood.schemas.restaurant.RestaurantInfo;
import org.flinkfood.schemas.restaurant.RestaurantView;

public class CoGroupFunctionImpl_ implements CoGroupFunction<RestaurantInfo, RestaurantAddress, RestaurantView> {

    @Override
    public void coGroup(Iterable<RestaurantInfo> first, Iterable<RestaurantAddress> second, Collector<RestaurantView> out) throws Exception {
        first.forEach(restaurantInfo -> {
            var restaurantView = new RestaurantView().with(restaurantInfo);
            second.forEach(restaurantView::with);
            out.collect(restaurantView);
        });
    }
}

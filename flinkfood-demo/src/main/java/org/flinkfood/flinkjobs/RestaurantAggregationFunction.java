package org.flinkfood.flinkjobs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantAggregationFunction implements FlatMapFunction<Row, Row> {
    public RestaurantAggregationFunction() {

    }
    @Override
    public void flatMap(Row value, Collector<Row> out) {
        out.collect(value);
    }
}
class ViewCollector implements Collector<Row> {
    Map<Integer, Row> restaurants = new HashMap<>();
    @Override
    public void collect(Row record) {
        try {
            if (!restaurants.containsKey(record.getField("restaurant_id")))
                restaurants.put((Integer) record.getField("restaurant_id"), record);
            else
                this.add(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add(Row record) {
        Row restaurant = restaurants.get(record.getField("restaurant_id"));
        restaurant.setField("addresses", List.of(
                "street" + record.getField("street") +
                "number" + record.getField("number") +
                "zipCode" + record.getField("zipCode") +
                "city" + record.getField("city") +
                "province" + record.getField("province") +
                "country" + record.getField("country"),
                Objects.requireNonNull(restaurant.getField("addresses"))));
        restaurant.setField("services", List.of(
                "takeAway" + record.getField("takeAway") +
                "delivery" + record.getField("delivery") +
                "dineIn" + record.getField("dineIn") +
                "parkingLots" + record.getField("parkingLots") +
                "accessibleEntrance" + record.getField("accessibleEntrance") +
                "childrenArea" + record.getField("childrenArea") +
                "childrenFood" + record.getField("childrenFood"),
                Objects.requireNonNull(restaurant.getField("services"))));
    }

    @Override
    public void close() {
        restaurants = null;
    }
}
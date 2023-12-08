package org.flinkfood;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.List;

import static org.flinkfood.serializers.RestaurantRowToBsonDocument.mergeViews;

/**
 * By contruction is not parallelizable,
 * but it can be used to create a parallelizable function
 */
public class SVAggregator extends TableAggregateFunction<Row, SVAccumulator> {

    @Override
    public SVAccumulator createAccumulator() {
        return new SVAccumulator();
    }

    @Override
    public boolean isDeterministic() {
        return false; // depends on the order of the rows
    }

    /**
     * Implementation is based on the Restaurant View
     * @param acc
     * @param row
     */
    public void accumulate(SVAccumulator acc, Row row) {
        int restaurantId = (int) row.getField("id");
        if (acc.view.containsKey(restaurantId))
            acc.view.get(restaurantId).add(row);
        else
            acc.view.put(restaurantId, List.of(row));
    }

    /**
     * Implementation is based on the Restaurant View
     * @param acc as a list of Rows one for each element in the view
     * @param out contains a row for each restaurant -> view
     */
    public void emitValue(SVAccumulator acc, Collector<Row> out) {
        for (List<Row> rows : acc.view.values()) {
            Iterator<Row> it = rows.iterator();
            var RestaurantView = it.next();
            while (it.hasNext())
                RestaurantView = mergeViews(RestaurantView, it.next());
            out.collect(RestaurantView);
        }
    }
}

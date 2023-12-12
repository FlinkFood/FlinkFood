package org.flinkfood;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.flinkfood.serializers.RestaurantRowToBsonDocument.mergeViews;

/**
 * From {@link TableAggregateFunction} documentation:
 * For each set of rows that needs to be aggregated the runtime will create an empty accumulator by calling createAccumulator(). Subsequently, the accumulate() method of the function is called for each input row to update the accumulator. Once all rows have been processed, the emitValue() or emitUpdateWithRetract() method of the function is called to compute and return the final result.
 *
 * The aggregator aggregates
 *  orders
 *  TODO: find a modular way to aggregate all the attributes
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
     * Implementation is based on the Restaurant View to make it modular
     * is it even possible to make it modular?
     */
    public void accumulate(SVAccumulator acc, Row row) {
        for (String name : row.getFieldNames(true)) {// The idea is that the table attributes have a prefix that identifies the table.
            int index = acc.count;
            SVAttributes attr = SVAttributes.fromString(name);
            Object elem = row.getField(name);
            if (name.contains("order")) {
                acc.view.computeIfAbsent(attr, k -> new ArrayList<>()).get(index).setField(name, elem);
            } else if (name.contains("review")) {
                //etc
            } else
                row.setField(name, List.of(row.getField(name)));
        }
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

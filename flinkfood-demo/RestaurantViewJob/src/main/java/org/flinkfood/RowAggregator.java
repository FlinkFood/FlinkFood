package org.flinkfood;

import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * The idea was to map the big row in the smaller row with only
 * the attributes needed for the view (the parent attributes)
 */
public class RowAggregator extends AggregateFunction<Row, Row> {
    static final int N_VIEW_ATTRIBUTES = List.of("businessName", "servedDishes", "branches", "reviews", "promotions").size();

    @Override
    public Row getValue(Row accumulator) {
        return accumulator;
    }

    @Override
    public Row createAccumulator() {
        return new Row(N_VIEW_ATTRIBUTES);
    }
}

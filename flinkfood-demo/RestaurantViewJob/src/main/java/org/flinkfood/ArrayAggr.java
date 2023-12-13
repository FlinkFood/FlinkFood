package org.flinkfood;

import org.apache.flink.table.api.dataview.ListView;
import org.apache.flink.table.functions.AggregateFunction;

public class ArrayAggr <T> extends AggregateFunction<T[], ListView<T>> {
    @Override
    public T[] getValue(ListView<T> accumulator) {
        return null;
    }

    @Override
    public ListView<T> createAccumulator() {
        return new ListView<T>();
    }

}

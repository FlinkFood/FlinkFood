package org.flinkfood;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.dataview.ListView;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.FunctionRequirement;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;

import java.util.*;
import java.util.jar.Attributes;

public class SVAccumulator {
    /**
     * The accumulator accumulates all attribute of a view
     */
     public ListView<Row> attributes = new ListView<>();



    public SVAccumulator() {
    }

    public void add(Row row) throws Exception {
        attributes.add(row);
    }

    /**
     * Merge two accumulators together
     * Needed for parallelization
     */
    public void merge(SVAccumulator other) throws Exception {
        attributes.addAll(other.attributes.getList());
    }
}


package org.flinkfood;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.FunctionRequirement;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;

public class SVAccumulator {
    /**
     * Accumulator for the view each.
     * Each entry is a view.
     * Map<SVAttributes, Row> could also be a `Row` but this way is more explicit!
     * //TOO COMPLEX FOR NOW; will just try to use a List<Row> and see if it works
     */
    public Map<Integer,
            List<Row>> view;
        //  Map<SVAttributes, Row>> view;



    public SVAccumulator() {
        view = new HashMap<>();

    }

    public Map<Integer,List<Row>> getView() {
        return view;
    }

    public void setView(Map<Integer,List<Row>> view) {
        this.view = view;
    }
}


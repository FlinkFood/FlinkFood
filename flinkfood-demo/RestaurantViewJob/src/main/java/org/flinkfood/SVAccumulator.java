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
     * each list is a container for the rows
     * at the same index in each list, the rows have the same id ?! makes sense?
     */
     public Map<SVAttributes, List<Row>> view;
     public int count;



    public SVAccumulator() {
        view = new HashMap<>();
        count = 0;
    }
}


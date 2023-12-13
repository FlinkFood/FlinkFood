package org.flinkfood;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.InputGroup;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.w3c.dom.TypeInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.flinkfood.serializers.RestaurantRowToBsonDocument.mergeViews;

/**
 * From {@link TableAggregateFunction} documentation:
 * For each set of rows that needs to be aggregated the runtime will create an empty accumulator by calling createAccumulator().
 * Subsequently, the accumulate() method of the function is called for each input row to update the accumulator.
 * Once all rows have been processed, the emitValue() or emitUpdateWithRetract() method
 * of the function is called to compute and return the final result.
 */
public class SVAggregator extends TableAggregateFunction<Row, SVAccumulator> {

    @Override
    public SVAccumulator createAccumulator() {
        return new SVAccumulator();
    }

    /**
     * The second parameter must be defined, can't be a {@code Row}. There are problems with the type inference.
     * I tried also@using {@code DataTypeHint(inputGroup = InputGroup.ANY) Object... fields} but it doesn't work.
     */
    public void accumulate(SVAccumulator acc, Row row) throws Exception {
        acc.add(row);
    }

    /**
     * Implementation is based on the Restaurant View
     * @param acc as a list of Rows one for each element in the view
     * @param out contains a row for each restaurant -> view
     */
    public void emitValue(SVAccumulator acc, Collector<Row> out) {
        var merged_rows = new Row(1);
        //FIXME: give the right name to the field
        merged_rows.setField("dishes", acc.attributes);
        out.collect(merged_rows);
    }

    @Override
    public TypeInformation<Row> getResultType() {
        return Types.ROW();
    }

}

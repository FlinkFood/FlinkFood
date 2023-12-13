package org.flinkfood;

import org.apache.flink.table.api.dataview.ListView;

import java.util.Objects;

public class ArrayAccumulator<T> {

    public ListView<T> values = new ListView<T>();

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArrayAccumulator<?> other = (ArrayAccumulator<?>) obj;
        return Objects.equals(values, other.values);
    }
}
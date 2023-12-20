package org.flinkfood.supportClasses;

import org.apache.flink.table.api.dataview.ListView;

import java.util.Objects;

/**
 * This class is used to accumulate the values of the array of a table
 * It is used in the RestaurantViewJob to create the view
 * @param <T> the type of the array to accumulate (in our case {@code Row})
 */
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
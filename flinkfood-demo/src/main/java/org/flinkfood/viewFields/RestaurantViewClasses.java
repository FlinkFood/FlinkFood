package org.flinkfood.viewFields;

import org.flinkfood.schemas.order.Order;
import org.flinkfood.schemas.restaurant.RestaurantInfo;

//i wanted to use the enum to have a better modularity, but didn't implement it
public enum RestaurantViewClasses {
    INFO("restaurant_info", RestaurantInfo.class),
    ORDER("restaurant_order", Order.class);

    private final String name;
    private final Class<?> clazz;

    RestaurantViewClasses(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return this.name;
    }
}

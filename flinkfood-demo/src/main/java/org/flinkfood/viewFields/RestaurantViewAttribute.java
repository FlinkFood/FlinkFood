package org.flinkfood.viewFields;

import java.util.List;

public enum RestaurantViewAttribute {
    SERVICES("services", List.of("parkingLots", "accessible", "childrenArea", "childrenFood")),
    REVIEWS("reviews", List.of("id", "restaurantId", "userId", "rating", "comment", "date")),
    ADDRESS("address", List.of("street", "number", "zipCode", "city", "province", "country")),
    STATUS("status", List.of("id", "name", "vatCode", "email"));


    private final String name;
    private final List<String> attributes;

    RestaurantViewAttribute(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getAttributes() {
        return attributes;
    }
}

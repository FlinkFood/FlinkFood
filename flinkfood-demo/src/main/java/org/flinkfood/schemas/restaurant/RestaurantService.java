package org.flinkfood.schemas.restaurant;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RestaurantService {
    private int restaurant_id;
    private String name;
    private String phone;
    private String email;
    private String cuisine_type;
    private String price_range;
    private int vat_code;

    public RestaurantService(int restaurant_id, String name, String phone, String email, String cuisine_type, String price_range, int vat_code) {
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cuisine_type = cuisine_type;
        this.price_range = price_range;
        this.vat_code = vat_code;
    }

    public RestaurantService(RestaurantService restaurantService) {
        this.restaurant_id = restaurantService.getRestaurant_id();
        this.name = restaurantService.getName();
        this.phone = restaurantService.getPhone();
        this.email = restaurantService.getEmail();
        this.cuisine_type = restaurantService.getCuisine_type();
        this.price_range = restaurantService.getPrice_range();
        this.vat_code = restaurantService.getVat_code();
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCuisine_type() {
        return cuisine_type;
    }

    public void setCuisine_type(String cuisine_type) {
        this.cuisine_type = cuisine_type;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public int getVat_code() {
        return vat_code;
    }

    public void setVat_code(int vat_code) {
        this.vat_code = vat_code;
    }

    public static class Deserializer extends AbstractDeserializationSchema<RestaurantService> {
        private static final long serialVersionUID = 1L;
        private transient ObjectMapper objectMapper;

        @Override
        public void open(DeserializationSchema.InitializationContext context) {
            objectMapper = new ObjectMapper();
        }

        @Override
        public RestaurantService deserialize(byte[] message) throws IOException {
            return objectMapper.readValue(message, RestaurantService.class);
        }
    }
}
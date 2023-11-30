package org.flinkfood.schemas.restaurant;

import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.mongodb.sink.config.MongoWriteOptions;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonDocument;

import java.io.IOException;

public class RestaurantInfo {

    public int id;
    public String name;
    public String phone;
    public String email;
    public String cuisine_type;
    public String city;
    public String price_range;
    public String vat_code;

    public RestaurantInfo(int id, String name, String phone, String email, String cuisine_type, String city, String price_range, String vat_code) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cuisine_type = cuisine_type;
        this.city = city;
        this.price_range = price_range;
        this.vat_code = vat_code;
    }

    public RestaurantInfo(RestaurantInfo restaurantInfo) {
        new RestaurantInfo(restaurantInfo.getId(), restaurantInfo.getName(), restaurantInfo.getPhone(), restaurantInfo.getEmail(), restaurantInfo.getCuisine_type(), restaurantInfo.getCity(), restaurantInfo.getPrice_range(), restaurantInfo.getVat_code());
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    // Setter for cuisineType
    public void setCuisine_type(String cuisine_type) {
        this.cuisine_type = cuisine_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public String getVat_code() {
        return vat_code;
    }

    public void setVat_code(String vat_code) {
        this.vat_code = vat_code;
    }

    private RestaurantInfo getRestaurantInfo() {
        return this;
    }
    public static class Deserializer extends AbstractDeserializationSchema<RestaurantInfo> {

        private static final long serialVersionUID = 1L;

        private transient ObjectMapper objectMapper;

        @Override
        public void open(DeserializationSchema.InitializationContext context) {
            objectMapper = new ObjectMapper();
        }
        @Override
        public RestaurantInfo deserialize(byte[] message) throws IOException {
            return objectMapper.readValue(message, RestaurantInfo.class);
        }
    }
    public static class Serializer implements MongoSerializationSchema<RestaurantInfo> {
        @Override
        public WriteModel<BsonDocument> serialize(RestaurantInfo element, MongoSinkContext sinkContext) {
                return new Serializer().serialize(element.getRestaurantInfo(), sinkContext);
        }
    }


}
package org.flinkfood.schemas.restaurant;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.flinkfood._helper.InsertBsonField;

import java.io.IOException;

public class RestaurantService implements InsertBsonField {
    //TODO
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
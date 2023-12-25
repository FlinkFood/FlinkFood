package org.flinkfood.schemas.restaurant;

import java.io.IOException;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.flinkfood.schemas.restaurant.RestaurantInfo;

public class KafkaRestaurantInfoSchema extends AbstractDeserializationSchema<RestaurantInfo> {

    private static final long serialVersionUID = 1L;
    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public RestaurantInfo deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, RestaurantInfo.class);
    }

}
package org.flinkfood.schemas;

import java.io.IOException;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaDishSchema extends AbstractDeserializationSchema<Dish> {

    private static final long serialVersionUID = 1L;
    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public Dish deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, Dish.class);
    }

}
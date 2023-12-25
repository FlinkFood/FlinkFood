package org.flinkfood.schemas.ingredient;

import java.io.IOException;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaIngredientSchema extends AbstractDeserializationSchema<Ingredient> {

    private static final long serialVersionUID = 1L;
    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public Ingredient deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, Ingredient.class);
    }

}
package org.flinkfood.schemas;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;

public class KafkaCustomerSchema extends AbstractDeserializationSchema<Customer> {

    private static final long serialVersionUID = 1L;
    private transient ObjectMapper objectMapper;

    @Override
    public void open(InitializationContext context) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public Customer deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, Customer.class);
    }

}

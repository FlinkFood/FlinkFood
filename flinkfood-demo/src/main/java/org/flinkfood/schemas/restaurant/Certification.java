package org.flinkfood.schemas.restaurant;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.bson.BsonDocument;
import org.flinkfood._helper.InsertBsonField;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "supplier_id"
})
public class Certification implements Serializable
{

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("supplier_id")
    private Integer supplierId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 2410056325984420178L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Certification() {
    }

    /**
     *
     * @param supplierId
     * @param name
     * @param id
     */
    public Certification(Integer id, String name, Integer supplierId) {
        super();
        this.id = id;
        this.name = name;
        this.supplierId = supplierId;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public Certification withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Certification withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("supplier_id")
    public Integer getSupplierId() {
        return supplierId;
    }

    @JsonProperty("supplier_id")
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Certification withSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Certification withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.supplierId == null)? 0 :this.supplierId.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Certification) == false) {
            return false;
        }
        Certification rhs = ((Certification) other);
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.supplierId == rhs.supplierId)||((this.supplierId!= null)&&this.supplierId.equals(rhs.supplierId))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

    public static class Deserializer extends AbstractDeserializationSchema<Certification> {
        private static final long serialVersionUID = 1L;

        ObjectMapper mapper;

        @Override
        public void open(InitializationContext context) {
            mapper = new ObjectMapper();
        }

        @Override
        public Certification deserialize(byte[] message) throws IOException {
            return mapper.readValue(message, Certification.class);
        }
    }
    public static class Serializer implements MongoSerializationSchema<Certification>, InsertBsonField {
        @Override
        public WriteModel<BsonDocument> serialize(Certification element, MongoSinkContext sinkContext) {
            // serialisation for final Document in MongoDB view
            var doc = new BsonDocument();
            addFieldToDocument(doc, "id", element.getId());
            addFieldToDocument(doc, "name", element.getName());
            addFieldToDocument(doc, "supplier_id", element.getSupplierId());
            return new InsertOneModel<>(doc);
        }

    }

}
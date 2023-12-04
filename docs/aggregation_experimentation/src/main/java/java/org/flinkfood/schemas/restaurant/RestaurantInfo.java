package org.flinkfood.schemas.restaurant;

import com.fasterxml.jackson.annotation.*;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.mongodb.sink.config.MongoWriteOptions;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.flinkfood._helper.InsertBsonField;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "phone",
        "email",
        "cuisine_type",
        "price_range",
        "vat_code"
})
public class RestaurantInfo implements Serializable, InsertBsonField
{

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("email")
    private String email;
    @JsonProperty("cuisine_type")
    private String cuisineType;
    @JsonProperty("price_range")
    private String priceRange;
    @JsonProperty("vat_code")
    private Integer vatCode;
    private final static long serialVersionUID = 5491799531008655767L;

    /**
     * No args constructor for use in serialization
     *
     */
    public RestaurantInfo() {
    }

    /**
     *
     * @param phone
     * @param name
     * @param id
     * @param cuisineType
     * @param priceRange
     * @param email
     * @param vatCode
     */
    public RestaurantInfo(Integer id, String name, String phone, String email, String cuisineType, String priceRange, Integer vatCode) {
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cuisineType = cuisineType;
        this.priceRange = priceRange;
        this.vatCode = vatCode;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public RestaurantInfo withId(Integer id) {
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

    public RestaurantInfo withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public RestaurantInfo withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public RestaurantInfo withEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonProperty("cuisine_type")
    public String getCuisineType() {
        return cuisineType;
    }

    @JsonProperty("cuisine_type")
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public RestaurantInfo withCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
        return this;
    }

    @JsonProperty("price_range")
    public String getPriceRange() {
        return priceRange;
    }

    @JsonProperty("price_range")
    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public RestaurantInfo withPriceRange(String priceRange) {
        this.priceRange = priceRange;
        return this;
    }

    @JsonProperty("vat_code")
    public Integer getVatCode() {
        return vatCode;
    }

    @JsonProperty("vat_code")
    public void setVatCode(Integer vatCode) {
        this.vatCode = vatCode;
    }

    public RestaurantInfo withVatCode(Integer vatCode) {
        this.vatCode = vatCode;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.phone == null)? 0 :this.phone.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.cuisineType == null)? 0 :this.cuisineType.hashCode()));
        result = ((result* 31)+((this.priceRange == null)? 0 :this.priceRange.hashCode()));
        result = ((result* 31)+((this.email == null)? 0 :this.email.hashCode()));
        result = ((result* 31)+((this.vatCode == null)? 0 :this.vatCode.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RestaurantInfo)) {
            return false;
        }
        RestaurantInfo rhs = ((RestaurantInfo) other);
        return Objects.equals(this.phone, rhs.phone) &&
                Objects.equals(this.name, rhs.name) &&
                Objects.equals(this.id, rhs.id) &&
                Objects.equals(this.cuisineType, rhs.cuisineType) &&
                (Objects.equals(this.priceRange, rhs.priceRange)) &&
                (Objects.equals(this.email, rhs.email)) &&
                (Objects.equals(this.vatCode, rhs.vatCode));
    }

    @Override
    public String toString() {
        return "RestaurantInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", priceRange='" + priceRange + '\'' +
                ", vatCode=" + vatCode +
                '}';
    }

    /*
     * Document composition, null safe
     */
    public BsonDocument toBson() {
        var doc = new BsonDocument();
        addFieldToDocument(doc, "id", id);
        addFieldToDocument(doc, "name", name);
        addFieldToDocument(doc, "phone", phone);
        addFieldToDocument(doc, "email", email);
        addFieldToDocument(doc, "cuisine_type", cuisineType);
        addFieldToDocument(doc, "price_range", priceRange);
        addFieldToDocument(doc, "vat_code", vatCode);
        return doc;
    }

    public static class Deserializer extends AbstractDeserializationSchema<RestaurantInfo> {

        private static final long serialVersionUID = 1L;

        com.fasterxml.jackson.databind.ObjectMapper mapper;

        @Override
        public void open(InitializationContext context) {
            mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        }
        @Override
        public RestaurantInfo deserialize(byte[] message) throws IOException {
            return mapper.readValue(message, RestaurantInfo.class);
        }
    }
    public static class Serializer implements MongoSerializationSchema<RestaurantInfo>, InsertBsonField {
        @Override
        public void open(SerializationSchema.InitializationContext initializationContext, MongoSinkContext sinkContext, MongoWriteOptions sinkConfiguration) throws Exception {
            initializationContext.getMetricGroup().addGroup("MongoDB");
            System.out.printf("test RestaurantInfo");
        }

        @Override
        public WriteModel<BsonDocument> serialize(RestaurantInfo element, MongoSinkContext sinkContext) {
            var doc = new BsonDocument();
            addFieldToDocument(doc, "id", element.getId());
            addFieldToDocument(doc, "name", element.getName());
            addFieldToDocument(doc, "phone", element.getPhone());
            addFieldToDocument(doc, "email", element.getEmail());
            addFieldToDocument(doc, "cuisine_type", element.getCuisineType());
            addFieldToDocument(doc, "price_range", element.getPriceRange());
            addFieldToDocument(doc, "vat_code", element.getVatCode());
            return new InsertOneModel<>(doc);
        }
    }
}
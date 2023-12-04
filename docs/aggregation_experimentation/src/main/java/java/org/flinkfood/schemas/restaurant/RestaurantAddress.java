package org.flinkfood.schemas.restaurant; ;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.flinkfood._helper.InsertBsonField;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "restaurant_id",
        "street",
        "address_number",
        "zip_code",
        "city",
        "province",
        "country"
})
public class RestaurantAddress implements Serializable, InsertBsonField {

    @JsonProperty("restaurant_id")
    private Integer restaurantId;
    @JsonProperty("street")
    private String street;
    @JsonProperty("address_number")
    private String addressNumber;
    @JsonProperty("zip_code")
    private Integer zipCode;
    @JsonProperty("city")
    private String city;
    @JsonProperty("province")
    private String province;
    @JsonProperty("country")
    private String country;
    private final static long serialVersionUID = 3416643064133019568L;

    /**
     * No args constructor for use in serialization
     */
    public RestaurantAddress() {
    }

    /**
     * @param zipCode
     * @param country
     * @param province
     * @param city
     * @param street
     * @param addressNumber
     * @param restaurantId
     */
    public RestaurantAddress(Integer restaurantId, String street, String addressNumber, Integer zipCode, String city, String province, String country) {
        super();
        this.restaurantId = restaurantId;
        this.street = street;
        this.addressNumber = addressNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    @JsonProperty("restaurant_id")
    public Integer getRestaurantId() {
        return restaurantId;
    }

    @JsonProperty("restaurant_id")
    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public RestaurantAddress withRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    public RestaurantAddress withStreet(String street) {
        this.street = street;
        return this;
    }

    @JsonProperty("address_number")
    public String getAddressNumber() {
        return addressNumber;
    }

    @JsonProperty("address_number")
    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public RestaurantAddress withAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
        return this;
    }

    @JsonProperty("zip_code")
    public Integer getZipCode() {
        return zipCode;
    }

    @JsonProperty("zip_code")
    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public RestaurantAddress withZipCode(Integer zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    public RestaurantAddress withCity(String city) {
        this.city = city;
        return this;
    }

    @JsonProperty("province")
    public String getProvince() {
        return province;
    }

    @JsonProperty("province")
    public void setProvince(String province) {
        this.province = province;
    }

    public RestaurantAddress withProvince(String province) {
        this.province = province;
        return this;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    public RestaurantAddress withCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RestaurantAddress.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("restaurantId");
        sb.append('=');
        sb.append(((this.restaurantId == null) ? "<null>" : this.restaurantId));
        sb.append(',');
        sb.append("street");
        sb.append('=');
        sb.append(((this.street == null) ? "<null>" : this.street));
        sb.append(',');
        sb.append("addressNumber");
        sb.append('=');
        sb.append(((this.addressNumber == null) ? "<null>" : this.addressNumber));
        sb.append(',');
        sb.append("zipCode");
        sb.append('=');
        sb.append(((this.zipCode == null) ? "<null>" : this.zipCode));
        sb.append(',');
        sb.append("city");
        sb.append('=');
        sb.append(((this.city == null) ? "<null>" : this.city));
        sb.append(',');
        sb.append("province");
        sb.append('=');
        sb.append(((this.province == null) ? "<null>" : this.province));
        sb.append(',');
        sb.append("country");
        sb.append('=');
        sb.append(((this.country == null) ? "<null>" : this.country));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.zipCode == null) ? 0 : this.zipCode.hashCode()));
        result = ((result * 31) + ((this.country == null) ? 0 : this.country.hashCode()));
        result = ((result * 31) + ((this.province == null) ? 0 : this.province.hashCode()));
        result = ((result * 31) + ((this.city == null) ? 0 : this.city.hashCode()));
        result = ((result * 31) + ((this.street == null) ? 0 : this.street.hashCode()));
        result = ((result * 31) + ((this.addressNumber == null) ? 0 : this.addressNumber.hashCode()));
        result = ((result * 31) + ((this.restaurantId == null) ? 0 : this.restaurantId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RestaurantAddress)) {
            return false;
        }
        RestaurantAddress rhs = ((RestaurantAddress) other);
        return  Objects.equals(this.zipCode, rhs.zipCode) &&
                Objects.equals(this.country, rhs.country) &&
                Objects.equals(this.province, rhs.province) &&
                Objects.equals(this.city, rhs.city) &&
                Objects.equals(this.street, rhs.street) &&
                Objects.equals(this.addressNumber, rhs.addressNumber) &&
                Objects.equals(this.restaurantId, rhs.restaurantId);
    }

    public BsonDocument toBson() {
        var doc = new BsonDocument();
        addFieldToDocument(doc, "restaurant_id", restaurantId);
        addFieldToDocument(doc, "street", street);
        addFieldToDocument(doc, "address_number", addressNumber);
        addFieldToDocument(doc, "zip_code", zipCode);
        addFieldToDocument(doc, "city", city);
        addFieldToDocument(doc, "province", province);
        addFieldToDocument(doc, "country", country);
        return doc;
    }

    public static class Deserializer extends AbstractDeserializationSchema<RestaurantAddress> {
        private static final long serialVersionUID = 1L;
        ObjectMapper mapper;
        @Override
        public void open(InitializationContext context) {
            mapper = new ObjectMapper();
        }
        @Override
        public RestaurantAddress deserialize(byte[] message) throws IOException {
            return mapper.readValue(message, RestaurantAddress.class);
        }
    }

    public static class Serializer implements MongoSerializationSchema<RestaurantAddress>, InsertBsonField {
        @Override
        public WriteModel<BsonDocument> serialize(RestaurantAddress element, MongoSinkContext sinkContext) {
            var doc = new BsonDocument();
            addFieldToDocument(doc, "restaurant_id", element.getRestaurantId());
            addFieldToDocument(doc, "street", element.getStreet());
            addFieldToDocument(doc, "address_number", element.getAddressNumber());
            addFieldToDocument(doc, "zip_code", element.getZipCode());
            addFieldToDocument(doc, "city", element.getCity());
            addFieldToDocument(doc, "province", element.getProvince());
            addFieldToDocument(doc, "country", element.getCountry());
            return new InsertOneModel<>(doc);
        }

    }
}
package org.flinkfood.schemas.restaurant;

import org.bson.BsonValue;

public class RestaurantAddress {
    private String street;
    private String city;
    private String state;
    private String zip_code;
    private String country;

    public RestaurantAddress(String street, String city, String state, String zip_code, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.country = country;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

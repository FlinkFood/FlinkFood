package org.flinkfood.schemas;

public class Address {

    public int id;
    public int customer_id;
    public String street;
    public int street_number;
    public String zipcode;
    public String city;
    public String province;
    public String country;
    public boolean isdelivery;
    public boolean ispayment;

    public Address() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int user_id) {
        this.customer_id = user_id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStreet_number() {
        return street_number;
    }

    public void setStreet_number(int street_number) {
        this.street_number = street_number;
    }

    public String getZipCode() {
        return zipcode;
    }

    public void setZipCode(String zipCode) {
        this.zipcode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDelivery() {
        return isdelivery;
    }

    public void setDelivery(boolean isDelivery) {
        this.isdelivery = isDelivery;
    }

    public boolean isPayment() {
        return ispayment;
    }

    public void setPayment(boolean isPayment) {
        this.ispayment = isPayment;
    }

    @Override
    public String toString() {
        return "Address [id=" + id + ", customer_id=" + customer_id + ", street=" + street + ", street_number="
                + street_number + ", zipcode=" + zipcode + ", city=" + city + ", province=" + province + ", country="
                + country + ", isdelivery=" + isdelivery + ", ispayment=" + ispayment + "]";
    }

}

package org.flinkfood.flinkjobs;

public class Customer_address {

    public int id;
    public int customer_id;
    public String street;
    public int address_number;
    public String zip_code;
    public String city;
    public String province;
    public String country;

    public Customer_address() {
    }

    public int getId() {
        return id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getStreet() {
        return street;
    }

    public int getStreet_number() {
        return address_number;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreet_number(int street_number) {
        this.address_number = street_number;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    
}

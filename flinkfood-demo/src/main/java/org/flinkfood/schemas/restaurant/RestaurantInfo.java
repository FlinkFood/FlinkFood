package org.flinkfood.schemas.restaurant;

public class RestaurantInfo {

    public int id;
    public String name;
    public String phone;
    public String email;
    public String cuisine_type;
    public String city;
    public String price_range;
    public String vat_code;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCuisine_type() {
        return cuisine_type;
    }

    // Setter for cuisineType
    public void setCuisine_type(String cuisine_type) {
        this.cuisine_type = cuisine_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public String getVat_code() {
        return vat_code;
    }

    public void setVat_code(String vat_code) {
        this.vat_code = vat_code;
    }
}
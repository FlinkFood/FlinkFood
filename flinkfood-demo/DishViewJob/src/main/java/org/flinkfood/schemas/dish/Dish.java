package org.flinkfood.schemas.dish;

import java.util.List;

import org.flinkfood.schemas.ingredient.Ingredient;
import org.flinkfood.schemas.restaurant.RestaurantInfo;

public class Dish {

    public int id;
    public int restaurant_id;
    public RestaurantInfo restaurant_info;
    public List<Ingredient> ingredients;
    public String name;
    public int price;
    public String currency;
    public String category;
    public String description;

    public Dish() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurant_id() {
        return this.restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public RestaurantInfo getRestaurant_info() {
        return this.restaurant_info;
    }

    public void setRestaurant_info(RestaurantInfo restaurant_info) {
        this.restaurant_info = restaurant_info;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
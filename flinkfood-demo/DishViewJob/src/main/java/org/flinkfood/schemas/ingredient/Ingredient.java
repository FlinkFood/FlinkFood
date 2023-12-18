package org.flinkfood.schemas.ingredient;

public class Ingredient {

    public int id;
    public String name;
    public String description;
    public int carbs;
    public int proteins;
    public int fats;
    public int fibers;
    public int salt;
    public int calories;

    public Ingredient() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarbs() {
        return this.carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProteins() {
    return this.proteins;
    }

    public void setProteins(int proteins) {
        this.proteins = proteins;
    }

    public int getFats() {
        return this.fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getFibers() {
        return this.fibers;
    }

    public void setFibers(int fibers) {
        this.fibers = fibers;
    }

    public int getSalt() {
        return this.salt;
    }

    public void setSalt(int salt) {
        this.salt = salt;
    }

    public int getCalories() {
        return this.calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
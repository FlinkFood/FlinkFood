package org.flinkfood.schemas.dish_ingredient;

public class DishIngredient {

    public int id;
    public int dish_id;
    public int ingredient_id;
    public int supplier_id;

    public DishIngredient() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDish_id() {
        return this.dish_id;
    }

    public void setDish_id(int carbs) {
        this.dish_id = carbs;
    }

    public int getIngredient_id() {
    return this.ingredient_id;
    }

    public void setIngredient_id(int proteins) {
        this.ingredient_id = proteins;
    }

    public int getSupplier_id() {
        return this.supplier_id;
    }

    public void setSupplier_id(int fats) {
        this.supplier_id = fats;
    }
}
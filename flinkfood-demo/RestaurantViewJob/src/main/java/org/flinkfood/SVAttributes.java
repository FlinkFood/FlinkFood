package org.flinkfood;

//TODO: add all the parameters or find a way to make this generic
enum SVAttributes {
    REVIEWS,
    DISHES;

    public static SVAttributes fromString(String s) {
        switch (s) {
            case "reviews":
                return REVIEWS;
            case "dishes":
                return DISHES;
            default:
                throw new IllegalArgumentException("Invalid SVAttribute: " + s);
        }
    }
}

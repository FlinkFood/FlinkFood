-- POSTGRES DIALECT

CREATE TABLE IF NOT EXISTS restaurants (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(511),
    phone VARCHAR(20),
    email VARCHAR(255),
    cuisine VARCHAR(255),
    price_range VARCHAR(10),
    rating DECIMAL(3,2),
    -- the following can be included as services (separate table)
    take_away BOOLEAN,
    delivery BOOLEAN,
    dine_in BOOLEAN,
    parking_lots INT
    );

CREATE TABLE IF NOT EXISTS dishes (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(5,2),
    description VARCHAR(255),
    rating DECIMAL(3,2)
    );

CREATE TABLE IF NOT EXISTS served_dishes (
    id INT PRIMARY KEY,
    restaurant_id INT,
    dish_id INT,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
    );


CREATE TABLE IF NOT EXISTS reviews_restaurant(
    id INT PRIMARY KEY,
    restaurant_id INT,
    rating DECIMAL(3,2),
    comment VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
    );

CREATE TABLE IF NOT EXISTS reviews_dish(
    id INT PRIMARY KEY,
    dish_id INT,
    rating DECIMAL(3,2),
    comment VARCHAR(255),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
    );

CREATE TABLE IF NOT EXISTS ingredients (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
    );
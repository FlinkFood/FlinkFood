-- POSTGRES DIALECT

CREATE TABLE IF NOT EXISTS restaurants (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(511),
    phone VARCHAR(20),
    email VARCHAR(255),
    cuisine VARCHAR(255),
    price_range VARCHAR(10),
    rating DECIMAL(3,1),
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
    rating DECIMAL(3,1)
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
    rating DECIMAL(3,1),
    comment VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
    );

CREATE TABLE IF NOT EXISTS reviews_dish(
    id INT PRIMARY KEY,
    dish_id INT,
    rating DECIMAL(3,1),
    comment VARCHAR(255),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
    );

CREATE TABLE IF NOT EXISTS ingredients (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
    );

-- id is not included such that is automatically generated 
COPY restaurants (name, address, phone, email, cuisine, price_range, rating, take_away, delivery, dine_in, parking_lots)
    FROM '/docker-entrypoint-initdb.d/csv/restaurants.csv' DELIMITER ',' CSV HEADER;

copy dishes (name, price, description, rating)
    from '/docker-entrypoint-initdb.d/csv/dishes.csv' delimiter ',' csv header;

copy served_dishes (restaurant_id, dish_id)
    from '/docker-entrypoint-initdb.d/csv/served_dishes.csv' delimiter ',' csv header;

copy reviews_restaurant (restaurant_id, rating, comment)
    from '/docker-entrypoint-initdb.d/csv/reviews_restaurant.csv' delimiter ',' csv header;

copy reviews_dish (dish_id, rating, comment)
    from '/docker-entrypoint-initdb.d/csv/reviews_dish.csv' delimiter ',' csv header;

copy ingredients (name, description)
    from '/docker-entrypoint-initdb.d/csv/ingredients.csv' delimiter ',' csv header;

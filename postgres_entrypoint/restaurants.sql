-- POSTGRES DIALECT

CREATE TABLE IF NOT EXISTS restaurant_info (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    cuisine_type VARCHAR(255),
    price_range VARCHAR(10),
    vat_code INT
);

CREATE TABLE IF NOT EXISTS supplier (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    vat_code INT,
    country VARCHAR(255),
    is_sustainable BOOLEAN
);

CREATE TABLE IF NOT EXISTS customer (
    id serial PRIMARY KEY,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birthdate DATE NOT NULL,
    email VARCHAR(255),
    fiscal_code VARCHAR(16)
);


CREATE TABLE IF NOT EXISTS restaurant_services (
    restaurant_id INT PRIMARY KEY, 
    take_away BOOLEAN,
    delivery BOOLEAN,
    dine_in BOOLEAN,
    parking_lots INT,
    accessible BOOLEAN, 
    children_area BOOLEAN,
    children_food BOOLEAN,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id)
    );

CREATE TABLE IF NOT EXISTS restaurant_address (
    restaurant_id INT PRIMARY KEY, 
    street VARCHAR(255),
    address_number VARCHAR(10),
    zip_code INT, 
    city VARCHAR(255),
    province VARCHAR(2),
    country VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id)
);

CREATE TABLE IF NOT EXISTS restaurant_reviews (
    id INT PRIMARY KEY,
    restaurant_id INT,
    customer_id INT,
    rating DECIMAL(3,1),
    comment VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
    );


CREATE TABLE IF NOT EXISTS dishes (
    id INT PRIMARY KEY,
    restaurant_id INT,
    name VARCHAR(255),
    price DECIMAL(5,2),
    currency VARCHAR(3),
    category VARCHAR(255),
    description VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id)
    );

CREATE TABLE IF NOT EXISTS dish_ingredients(
    id serial PRIMARY KEY, 
    dish_id INT,
    ingredient_id INT,
    supplier_id INT, 
    FOREIGN KEY (dish_id) REFERENCES dishes(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);


CREATE TABLE IF NOT EXISTS ingredients (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    carbs DECIMAL(5,2),
    proteins DECIMAL(5,2),
    fats DECIMAL(5,2),
    fibers DECIMAL(5,2),
    salt DECIMAL(5,2),
    calories DECIMAL(5,2)
    );

CREATE TABLE IF NOT EXISTS reviews_dish (
    id INT PRIMARY KEY,
    dish_id INT,
    customer_id INT, 
    rating DECIMAL(3,1),
    comment VARCHAR(255),
    FOREIGN KEY (dish_id) REFERENCES dishes(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
    );

CREATE TABLE IF NOT EXISTS customer_address (
    customer_id INT PRIMARY KEY, 
    street VARCHAR(255),
    address_number VARCHAR(10),
    zip_code INT, 
    city VARCHAR(255),
    province VARCHAR(2),
    country VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);


CREATE TABLE IF NOT EXISTS payment_methods (
    id serial PRIMARY KEY,
    customer_id INT,
    name VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer(id)

);

CREATE TABLE IF NOT EXISTS fidelity_card (
    id serial PRIMARY KEY,
    customer_id INT,
    card_number VARCHAR(255),
    points INT
);

CREATE TABLE IF NOT EXISTS orders (
    id serial PRIMARY KEY, 
    name VARCHAR(255),    -- name example given by the customer: ord-324567879-RAAC
    customer_id INT,
    restaurant_id INT,
    supplier_id INT, 
    order_date DATE,
    payment_date DATE,
    delivery_date DATE,
    description VARCHAR(255),
    total_amount DECIMAL(16,2),
    currency VARCHAR(3), 
    supply_order BOOLEAN, 
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id)
);


-- TABLE FOR THE ITEMS 
CREATE TABLE IF NOT EXISTS items (
    id serial PRIMARY KEY, 
    ingredient_id INT, 
    dish_id INT, 
    quantity INT, 
    order_id INT, 
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);


-- TABLE FOR CERTIFICATION
CREATE TABLE IF NOT EXISTS certifications (
    id serial PRIMARY KEY, 
    name VARCHAR(255), 
    supplier_id INT, 
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
    
);

-- TABLE FOR PARTNERSHIP
CREATE TABLE IF NOT EXISTS partnership (
    id serial PRIMARY KEY, 
    restaurant_id INT, 
    supplier_id INT, 
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
); 


-- id is not included such that is automatically generated 
COPY restaurant_info (name, phone, email, cuisine_type, price_range, vat_code)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_info.csv' DELIMITER ',' CSV HEADER;

copy dishes (restaurant_id, name, price, currency, category, description)
    from '/docker-entrypoint-initdb.d/imports/dishes.csv' delimiter ',' csv header;

COPY supplier (name, vat_code, country, is_sustainable)
    FROM '/docker-entrypoint-initdb.d/imports/supplier.csv' DELIMITER ',' CSV HEADER;

COPY customer (id, username, first_name, last_name, birthdate, email, fiscal_code)
    FROM '/docker-entrypoint-initdb.d/imports/customer.csv' DELIMITER ',' CSV HEADER;

COPY partnership (restaurant_id, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/partnership.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_services.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_address.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_reviews (id, restaurant_id, customer_id, rating, comment)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_reviews.csv' DELIMITER ',' CSV HEADER;

COPY dish_ingredients (dish_id, ingredient_id, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/dish_ingredients.csv' DELIMITER ',' CSV HEADER;

COPY ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories)
    FROM '/docker-entrypoint-initdb.d/imports/ingredients.csv' DELIMITER ',' CSV HEADER;

COPY reviews_dish (id, dish_id, customer_id, rating, comment)
    FROM '/docker-entrypoint-initdb.d/imports/reviews_dish.csv' DELIMITER ',' CSV HEADER;

COPY customer_address (customer_id, street, address_number, zip_code, city, province, country)
    FROM '/docker-entrypoint-initdb.d/imports/customer_address.csv' DELIMITER ',' CSV HEADER;

COPY payment_methods (customer_id, name)
    FROM '/docker-entrypoint-initdb.d/imports/payment_methods.csv' DELIMITER ',' CSV HEADER;

COPY fidelity_card (customer_id, card_number, points)
    FROM '/docker-entrypoint-initdb.d/imports/fidelity_card.csv' DELIMITER ',' CSV HEADER;

COPY orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order)
    FROM '/docker-entrypoint-initdb.d/imports/orders.csv' DELIMITER ',' CSV HEADER;

COPY items (ingredient_id, dish_id, quantity, order_id)
    FROM '/docker-entrypoint-initdb.d/imports/items.csv' DELIMITER ',' CSV HEADER;

COPY certifications (name, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/certifications.csv' DELIMITER ',' CSV HEADER;




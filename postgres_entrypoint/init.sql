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
ALTER TABLE restaurant_info REPLICA IDENTITY FULL;

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


CREATE TABLE IF NOT EXISTS restaurant_service (
    restaurant_id INT PRIMARY KEY,
    take_away BOOLEAN,
    delivery BOOLEAN,
    dine_in BOOLEAN,
    parking_lots INT,
    accessible BOOLEAN,
    children_area BOOLEAN,
    children_food BOOLEAN,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id)
    );
ALTER TABLE restaurant_service REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS restaurant_address (
    restaurant_id INT PRIMARY KEY,
    street VARCHAR(255),
    address_number VARCHAR(10),
    zip_code INT,
    city VARCHAR(255),
    province VARCHAR(2),
    country VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id)
);
ALTER TABLE restaurant_address REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS restaurant_review (
    id INT PRIMARY KEY,
    restaurant_id INT,
    customer_id INT,
    rating SMALLINT CHECK ( rating >= 0 and rating <= 10 ),
    comment VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
    );
ALTER TABLE restaurant_review REPLICA IDENTITY FULL;


CREATE TABLE IF NOT EXISTS dish (
    id serial PRIMARY KEY,
    restaurant_id INT,
    name VARCHAR(255),
    price SMALLINT CHECK ( price >= 0 ),
    currency VARCHAR(3),
    category VARCHAR(255),
    description VARCHAR(255),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id)
    );
ALTER TABLE dish REPLICA IDENTITY FULL;


CREATE TABLE IF NOT EXISTS ingredient (
   id INT PRIMARY KEY,
   name VARCHAR(255),
   description VARCHAR(255),
   carbs SMALLINT CHECK ( carbs >= 0 AND carbs <= 100 ), -- in grams
   proteins SMALLINT CHECK ( proteins >= 0 AND proteins <= 100),
   fats SMALLINT CHECK ( fats >= 0 AND fats <= 100),
   fibers SMALLINT CHECK ( fibers >= 0 AND fibers <= 100),
   salt SMALLINT CHECK ( salt >= 0 AND salt <= 100 ),
   calories INT CHECK ( calories >= 0 ) -- in kcal 
);
ALTER TABLE ingredient REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS dish_ingredient (
    id serial PRIMARY KEY,
    dish_id INT,
    ingredient_id INT,
    supplier_id INT,
    FOREIGN KEY (dish_id) REFERENCES dish (id),
    FOREIGN KEY (supplier_id) REFERENCES supplier (id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient (id)
);
ALTER TABLE dish_ingredient REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS dish_reviews (
    id INT PRIMARY KEY,
    dish_id INT,
    customer_id INT,
    rating SMALLINT CHECK ( rating >= 0 ),
    comment VARCHAR(255),
    FOREIGN KEY (dish_id) REFERENCES dish (id),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
    );
ALTER TABLE dish_reviews REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS customer_address (
    id serial PRIMARY KEY,
    customer_id INT,
    street VARCHAR(255),
    address_number VARCHAR(10),
    zip_code INT,
    city VARCHAR(255),
    province VARCHAR(2),
    country VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);


CREATE TABLE IF NOT EXISTS payment_method (
    id serial PRIMARY KEY,
    customer_id INT,
    name VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);
ALTER TABLE payment_method REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS fidelity_card (
    id serial PRIMARY KEY,
    customer_id INT,
    card_number VARCHAR(255),
    points INT
);
ALTER TABLE fidelity_card REPLICA IDENTITY FULL;

CREATE TABLE IF NOT EXISTS "order" (
    id            serial PRIMARY KEY,
    name          VARCHAR(255), -- name example given by the customer: ord-324567879-RAAC
    customer_id   INT,
    restaurant_id INT,
    supplier_id   INT,
    order_date    DATE,
    payment_date  DATE,
    delivery_date DATE,
    description   VARCHAR(255),
    total_amount  SMALLINT CHECK ( total_amount >= 0 ),
    currency      VARCHAR(3),
    supply_order  BOOLEAN,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    FOREIGN KEY (supplier_id) REFERENCES supplier (id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id)
);
ALTER TABLE "order" REPLICA IDENTITY FULL;

-- TABLE FOR THE ITEMS
CREATE TABLE IF NOT EXISTS item (
    id serial PRIMARY KEY,
    ingredient_id INT,
    dish_id INT,
    quantity INT,
    order_id INT,
    FOREIGN KEY (ingredient_id) REFERENCES ingredient (id),
    FOREIGN KEY (dish_id) REFERENCES dish (id),
    FOREIGN KEY (order_id) REFERENCES "order" (id)
);
ALTER TABLE item REPLICA IDENTITY FULL;


-- TABLE FOR CERTIFICATION
CREATE TABLE IF NOT EXISTS certification (
    id serial PRIMARY KEY,
    name VARCHAR(255),
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES supplier (id)
);
ALTER TABLE certification REPLICA IDENTITY FULL;

-- TABLE FOR PARTNERSHIP
CREATE TABLE IF NOT EXISTS partnership (
    id serial PRIMARY KEY,
    restaurant_id INT,
    supplier_id INT,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant_info (id),
    FOREIGN KEY (supplier_id) REFERENCES supplier (id)
);
ALTER TABLE partnership REPLICA IDENTITY FULL;

COPY restaurant_info (id, name, phone, email, cuisine_type, price_range, vat_code)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_info.csv' DELIMITER ',' CSV HEADER;

copy dish (id, restaurant_id, name, price, currency, category, description)
    from '/docker-entrypoint-initdb.d/imports/dish.csv' delimiter ',' csv header;

COPY supplier (id, name, vat_code, country, is_sustainable)
    FROM '/docker-entrypoint-initdb.d/imports/supplier.csv' DELIMITER ',' CSV HEADER;

COPY customer (id, username, first_name, last_name, birthdate, email, fiscal_code)
    FROM '/docker-entrypoint-initdb.d/imports/customer.csv' DELIMITER ',' CSV HEADER;

COPY partnership (id, restaurant_id, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/partnership.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_service (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_service.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_address.csv' DELIMITER ',' CSV HEADER;

COPY restaurant_review (id, restaurant_id, customer_id, rating, comment)
    FROM '/docker-entrypoint-initdb.d/imports/restaurant_review.csv' DELIMITER ',' CSV HEADER;

COPY ingredient (id, name, description, carbs, proteins, fats, fibers, salt, calories)
    FROM '/docker-entrypoint-initdb.d/imports/ingredient.csv' DELIMITER ',' CSV HEADER;

COPY dish_ingredient (id, dish_id, ingredient_id, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/dish_ingredient.csv' DELIMITER ',' CSV HEADER;

COPY reviews_dish (id, dish_id, customer_id, rating, comment)
    FROM '/docker-entrypoint-initdb.d/imports/reviews_dish.csv' DELIMITER ',' CSV HEADER;

COPY customer_address (id, customer_id, street, address_number, zip_code, city, province, country)
    FROM '/docker-entrypoint-initdb.d/imports/customer_address.csv' DELIMITER ',' CSV HEADER;

COPY payment_method (id, customer_id, name)
    FROM '/docker-entrypoint-initdb.d/imports/payment_method.csv' DELIMITER ',' CSV HEADER;

COPY fidelity_card (id, customer_id, card_number, points)
    FROM '/docker-entrypoint-initdb.d/imports/fidelity_card.csv' DELIMITER ',' CSV HEADER;

COPY "order" (id, name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order)
    FROM '/docker-entrypoint-initdb.d/imports/order.csv' DELIMITER ',' CSV HEADER;

COPY item (id, ingredient_id, dish_id, quantity, order_id)
    FROM '/docker-entrypoint-initdb.d/imports/item.csv' DELIMITER ',' CSV HEADER;

COPY certification (id, name, supplier_id)
    FROM '/docker-entrypoint-initdb.d/imports/certification.csv' DELIMITER ',' CSV HEADER;




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


-- TABLE restaurant_info
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES('Ristorante da Mario', '1234567890', 'mario.gmail.com', 'italian', 'low', 2345678901);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES('Rambo Kebab', '+39 351 79525892', 'rambo.gmail.com', 'arabic', 'medium', 1234538547);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Pasta Paradise', '+39 366 98765432', 'pasta.paradise@gmail.com', 'italian', 'high', 9876123456);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Sushi Sensation', '+39 320 12345678', 'sushi.sensation@yahoo.com', 'japanese', 'expensive', 6543987654);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Mexican Fiesta', '+39 333 87654321', 'mexican.fiesta@hotmail.com', 'mexican', 'medium', 8765123453);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Burger Bonanza', '+39 347 54321678', 'burger.bonanza@gmail.com', 'american', 'affordable', 3456789012);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Thai Temptations', '+39 366 87654321', 'thai.temptations@yahoo.com', 'thai', 'moderate', 5678901234);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Mediterranean Delight', '+39 338 98765432', 'med.delight@hotmail.com', 'mediterranean', 'high', 7890123456);
INSERT INTO restaurant_info (name, phone, email, cuisine_type, price_range, vat_code) VALUES ('Vegetarian Bliss', '+39 320 65432109', 'veg.bliss@gmail.com', 'vegetarian', 'moderate', 8901234567);
 
-- TABLE dishes
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (1, 'Spaghetti alla carbonara', 10.00, 'EUR', 'first course', 'Spaghetti with eggs, guanciale and pecorino cheese');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (1, 'Saltimbocca alla romana', 15.00, 'EUR', 'second course', 'Veal with prosciutto and sage');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (1, 'Tiramisù', 5.00, 'EUR', 'dessert', 'Coffee-flavoured Italian dessert');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (2, 'Kebab', 5.00, 'EUR', 'main course', 'Kebab with meat, salad and sauce');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (2, 'Falafel', 5.00, 'EUR', 'main course', 'Falafel with salad and sauce');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (2, 'Baklava', 3.00, 'EUR', 'dessert', 'Sweet pastry with nuts and honey');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (3, 'Spaghetti alla carbonara', 10.00, 'EUR', 'primo', 'Spaghetti with eggs, guanciale and pecorino cheese');
INSERT INTO dishes (restaurant_id, name, price, currency, category, description) VALUES (3, 'Saltimbocca alla romana', 15.00, 'EUR', 'secondo', 'Veal with prosciutto, but no sage');

-- TABLE supplier
INSERT INTO supplier (name, vat_code, country, is_sustainable) VALUES ('Fior di latte', 1234567890, 'Italy', true);
INSERT INTO supplier (name, vat_code, country, is_sustainable) VALUES ('Pasta di Gragnano', 2345678901, 'Italy', true);
INSERT INTO supplier (name, vat_code, country, is_sustainable) VALUES ('Carni Saporite', 3456789012, 'Italy', false);
INSERT INTO supplier (name, vat_code, country, is_sustainable) VALUES ('Pesce Fresco', 4567890123, 'Italy', true);

-- TABLE customer
INSERT INTO customer (username, first_name, last_name, birthdate, email, fiscal_code) VALUES ('mariogamer', 'Mario', 'Rossi', '1990-01-01', 'mario@gmail.com', 'RSSMRA90A01H501A');
INSERT INTO customer (username, first_name, last_name, birthdate, email, fiscal_code) VALUES ('johndoe123', 'John', 'Doe', '1985-05-15', 'john.doe@gmail.com', 'DOEJHN85M15H501B');
INSERT INTO customer (username, first_name, last_name, birthdate, email, fiscal_code) VALUES ('aliceinwonderland', 'Alice', 'Smith', '1992-09-30', 'alice.smith@yahoo.com', 'SMIALC92P30H501C');
INSERT INTO customer (username, first_name, last_name, birthdate, email, fiscal_code) VALUES ('harrypotterfan', 'Harry', 'Potter', '1988-07-31', 'harry.potter@hotmail.com', 'POTHRP88L31H501D');
INSERT INTO customer (username, first_name, last_name, birthdate, email, fiscal_code) VALUES ('guitarhero89', 'Alex', 'Johnson', '1989-03-20', 'alex.johnson@gmail.com', 'JHNALX89C20H501E');

-- TABLE restaurant_services
INSERT INTO restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) VALUES (1, true, true, true, 10, true, true, true);
INSERT INTO restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) VALUES (2, true, true, true, 5, true, true, true);
INSERT INTO restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) VALUES (3, true, true, true, 10, true, true, true);
INSERT INTO restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) VALUES (4, true, true, true, 10, true, true, true);
INSERT INTO restaurant_services (restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) VALUES (5, true, true, true, 10, true, true, true);

-- TABLE restaurant_address
INSERT INTO restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country) VALUES (1, 'Via Roma', '1', 12345, 'Roma', 'RM', 'Italy');
INSERT INTO restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country) VALUES (2, 'Via Milano', '2', 23456, 'Milano', 'MI', 'Italy');
INSERT INTO restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country) VALUES (3, 'Via Napoli', '3', 34567, 'Napoli', 'NA', 'Italy');
INSERT INTO restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country) VALUES (4, 'Via Firenze', '4', 45678, 'Firenze', 'FI', 'Italy');
INSERT INTO restaurant_address (restaurant_id, street, address_number, zip_code, city, province, country) VALUES (5, 'Via Venezia', '5', 56789, 'Venezia', 'VE', 'Italy');

-- TABLE restaurant_reviews
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (1, 1, 1, 4.5, 'Great food and service!');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (2, 1, 2, 4.0, 'Good food, but service could be better');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (3, 2, 3, 5.0, 'Best kebab in town!');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (4, 2, 4, 4.5, 'Great kebab, but a bit expensive');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (5, 3, 5, 4.0, 'Great food, but a bit expensive');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (6, 3, 1, 4.5, 'Great food and service!');
INSERT INTO restaurant_reviews (id, restaurant_id, customer_id, rating, comment) VALUES (7, 4, 2, 4.0, 'Good food, but service could be better');

-- TABLE dish_ingredients
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (1, 1, 1);
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (1, 2, 2);
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (1, 3, 3);
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (2, 4, 3);
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (2, 5, 3);
INSERT INTO dish_ingredients (dish_id, ingredient_id, supplier_id) VALUES (2, 6, 3);

-- TABLE ingredients
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (1, 'Spaghetti', 'Pasta', 75.0, 12.0, 1.5, 3.0, 0.0, 350.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (2, 'Guanciale', 'Pork cheek', 21.0, 88.0, 94.0, 0.5, 3.0, 255.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (3, 'Pecorino', 'Cheese', 1.0, 26.0, 24.0, 23.0, 3.0, 999.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (4, 'Veal', 'Meat', 220.0, 21.0, 3.0, 30.0, 2.0, 120.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (5, 'Salmon', 'Fresh salmon fillet', 0.0, 20.0, 10.0, 0.0, 1.5, 200.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (6, 'Avocado', 'Ripe avocado', 12.0, 2.0, 15.0, 10.0, 0.5, 160.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (7, 'Tomato', 'Fresh tomato', 5.0, 1.0, 0.5, 2.0, 0.1, 30.0);
INSERT INTO ingredients (id, name, description, carbs, proteins, fats, fibers, salt, calories) VALUES (8, 'Chicken Breast', 'Boneless, skinless chicken breast', 0.0, 25.0, 3.0, 0.0, 0.5, 120.0);

-- TABLE reviews_dish
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, comment) VALUES (1, 1, 1, 4.5, 'Good one for sure');
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, comment) VALUES (2, 1, 2, 4.0, 'My name is Optimus Prime');
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, comment) VALUES (3, 2, 3, 5.0, 'NOICE!');
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, comment) VALUES (4, 2, 4, 4.5, 'Good stuff, almost ate it all');
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, comment) VALUES (5, 3, 5, 4.0, 'Good, but not the best');

-- TABLE customer_address
INSERT INTO customer_address (customer_id, street, address_number, zip_code, city, province, country) VALUES (1, 'Via Roma', '1', 12345, 'Roma', 'RM', 'Italy');
INSERT INTO customer_address (customer_id, street, address_number, zip_code, city, province, country) VALUES (2, 'Main Street', '123', 56789, 'New York', 'NY', 'USA');
INSERT INTO customer_address (customer_id, street, address_number, zip_code, city, province, country) VALUES (3, 'Baker Street', '221B', 'SW1A 1AA', 'London', NULL, 'UK');
INSERT INTO customer_address (customer_id, street, address_number, zip_code, city, province, country) VALUES (4, 'Champs-Élysées', '75', '75008', 'Paris', NULL, 'France');
INSERT INTO customer_address (customer_id, street, address_number, zip_code, city, province, country) VALUES (5, 'Ginza', '4-2-15', '104-0061', 'Tokyo', NULL, 'Japan');

-- TABLE payment_methods
INSERT INTO payment_methods (customer_id, name) VALUES (1, 'Credit Card');
INSERT INTO payment_methods (customer_id, name) VALUES (1, 'PayPal');
INSERT INTO payment_methods (customer_id, name) VALUES (2, 'Credit Card');
INSERT INTO payment_methods (customer_id, name) VALUES (3, 'Credit Card');
INSERT INTO payment_methods (customer_id, name) VALUES (4, 'Credit Card');

-- TABLE fidelity_card
INSERT INTO fidelity_card (customer_id, card_number, points) VALUES (1, '1234567890123456', 100);
INSERT INTO fidelity_card (customer_id, card_number, points) VALUES (2, '2345678901234567', 200);
INSERT INTO fidelity_card (customer_id, card_number, points) VALUES (3, '3456789012345678', 300);
INSERT INTO fidelity_card (customer_id, card_number, points) VALUES (4, '4567890123456789', 400);
INSERT INTO fidelity_card (customer_id, card_number, points) VALUES (5, '5678901234567890', 500);

-- TABLE orders
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-324567879-RAAC', 1, 1, 2, '2021-05-01', '2021-05-01', '2021-05-01', 'Spaghetti alla carbonara', 10.00, 'EUR', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-789012345-SSJT', 2, 3, 2, '2021-06-15', '2021-06-15', '2021-06-16', 'Sushi Combo Deluxe', 35.00, 'USD', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-543210987-MXFT', 3, 2, 3, '2021-07-20', '2021-07-20', '2021-07-21', 'Tacos Fiesta Platter', 25.00, 'MXN', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-654321098-MDMD', 4, 4, 4, '2021-08-10', '2021-08-10', '2021-08-11', 'Mediterranean Feast', 50.00, 'EUR', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-123456789-VEGB', 5, 5, 5, '2021-09-05', '2021-09-05', '2021-09-06', 'Vegetarian Delight', 18.00, 'USD', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-987654321-THTD', 1, 1, 6, '2021-10-15', '2021-10-15', '2021-10-16', 'Teriyaki Chicken Bento', 15.00, 'JPY', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-234567890-QCTC', 2, 3, 7, '2021-11-02', '2021-11-02', '2021-11-03', 'Quinoa Chicken Salad', 12.00, 'USD', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-345678901-FRST', 3, 2, 8, '2021-12-05', '2021-12-05', '2021-12-06', 'Fajitas Fiesta', 28.00, 'MXN', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-876543210-PPSS', 4, 4, 9, '2022-01-20', '2022-01-20', '2022-01-21', 'Prosciutto and Pesto Sandwich', 8.00, 'EUR', false);
INSERT INTO orders (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('ord-567890123-VGPI', 5, 5, 10, '2022-02-12', '2022-02-12', '2022-02-13', 'Vegan Pizza', 20.00, 'USD', false);

-- TABLE items
INSERT INTO items (ingredient_id, dish_id, quantity, order_id) VALUES (1, 1, 1, 1);
INSERT INTO items (ingredient_id, dish_id, quantity, order_id) VALUES (2, 1, 1, 1);
INSERT INTO items (ingredient_id, dish_id, quantity, order_id) VALUES (3, 1, 1, 1);
INSERT INTO items (ingredient_id, dish_id, quantity, order_id) VALUES (4, 2, 1, 2);
INSERT INTO items (ingredient_id, dish_id, quantity, order_id) VALUES (5, 2, 1, 2);

-- TABLE certifications
INSERT INTO certifications (name, supplier_id) VALUES ('Organic', 1);
INSERT INTO certifications (name, supplier_id) VALUES ('Organic', 2);
INSERT INTO certifications (name, supplier_id) VALUES ('Nice', 3);


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




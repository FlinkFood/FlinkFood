--REGULAR CLIENTS
CREATE TABLE IF NOT EXISTS CLIENT
(
    ID serial PRIMARY KEY, 
    GENDER VARCHAR(1) NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    AGE INTEGER NOT NULL,
    FAVORITE_FOOD VARCHAR(45) NOT NULL,
    check(GENDER in ('M', 'F'))
);

--REGULAR CLIENTS
CREATE TABLE IF NOT EXISTS RESTAURANT
(
    ID serial PRIMARY KEY,
    NAME VARCHAR(40) NOT NULL,
    CITY VARCHAR(45) NOT NULL,
    STATE VARCHAR(45) NOT NULL
);

--DISHES
CREATE TABLE IF NOT EXISTS DISH
(
    ID serial PRIMARY KEY,
    NAME VARCHAR(40) NOT NULL,
    PRICE DECIMAL(5,2) NOT NULL,
    RATING DECIMAL(2,1) NOT NULL,
    RESTAURANT_ID INTEGER NOT NULL,
    CONSTRAINT FK_DISH_REST FOREIGN KEY(RESTAURANT_ID) REFERENCES RESTAURANT(ID) ON DELETE CASCADE
);

--REGULAR FOOD ALLERGY
CREATE TABLE IF NOT EXISTS FOOD_ALLERGY
(
    ID serial PRIMARY KEY,
    FOOD VARCHAR(40) NOT NULL
);

--ASSOCIATE CLIENTS WITH ALLERGIES
CREATE TABLE IF NOT EXISTS CLIENT_ALLERGY
(
    CLIENT_ID INTEGER,
    ALLERGY_ID INTEGER,
    PRIMARY KEY (CLIENT_ID, ALLERGY_ID),
    CONSTRAINT FK_ALLERGY_CLI FOREIGN KEY(CLIENT_ID) REFERENCES CLIENT(ID) ON DELETE CASCADE,
    CONSTRAINT FK_ALLERGY_FOOD FOREIGN KEY(ALLERGY_ID) REFERENCES FOOD_ALLERGY(ID) ON DELETE CASCADE
);
--This is just a PoC no need to add incrementing ID system in any table--

INSERT INTO CLIENT (GENDER, NAME, AGE, FAVORITE_FOOD) VALUES('M', 'MARCOS PETRUCCI', 23, 'PASTA AL POMODORO');
INSERT INTO CLIENT (GENDER, NAME, AGE, FAVORITE_FOOD) VALUES('M', 'GIOVANNI A', 23, 'PIZZA');

INSERT INTO RESTAURANT (NAME, CITY, STATE) VALUES
    ('pasta Palace', 'Rome', 'Lazio'),
    ('sushi Haven', 'Tokyo', 'Tokyo'),
    ('burger Bistro', 'New York', 'New York'),
    ('taco Town', 'Los Angeles', 'California'),
    ('curry Corner', 'Mumbai', 'Maharashtra');

-- Dishes for 'Pasta Palace' in Rome, Lazio
INSERT INTO DISH (NAME, PRICE, RATING, RESTAURANT_ID) VALUES
    ('Spaghetti Bolognese', 12.99, 4.5, 1),
    ('Carbonara', 10.99, 4.0, 1);

-- Dishes for 'Sushi Haven' in Tokyo, Tokyo
INSERT INTO DISH (NAME, PRICE, RATING, RESTAURANT_ID) VALUES
    ('Sushi Platter', 22.99, 4.8, 2),
    ('Sashimi Deluxe', 18.99, 4.5, 2);

-- Dishes for 'Burger Bistro' in New York, New York
INSERT INTO DISH (NAME, PRICE, RATING, RESTAURANT_ID) VALUES
    ('Classic Cheeseburger', 14.99, 4.3, 3),
    ('Veggie Burger', 12.99, 4.0, 3);

-- Dishes for 'Taco Town' in Los Angeles, California
INSERT INTO DISH (NAME, PRICE, RATING, RESTAURANT_ID) VALUES
    ('Taco Trio', 9.99, 4.2, 4),
    ('Quesadilla', 8.99, 4.1, 4);

-- Dishes for 'Curry Corner' in Mumbai, Maharashtra
INSERT INTO DISH (NAME, PRICE, RATING, RESTAURANT_ID) VALUES
    ('Chicken Tikka Masala', 15.99, 4.6, 5),
    ('Vegetable Curry', 12.99, 4.4, 5);


-- temporary table to choose random names to put on the table
CREATE TEMPORARY TABLE temp_names (name VARCHAR(40));
INSERT INTO temp_names (name)
VALUES
    ('Alice'),
    ('Bob'),
    ('Charlie'),
    ('David'),
    ('Eva'),
    ('Frank'),
    ('Grace'),
    ('Henry'),
    ('Ivy'),
    ('Jack'),
    ('Kate'),
    ('Leo'),
    ('Mia'),
    ('Noah'),
    ('Olivia'),
    ('Peter'),
    ('Quinn'),
    ('Rachel'),
    ('Sam'),
    ('Tina'),
    ('Ursula'),
    ('Victor'),
    ('Wendy'),
    ('Xander'),
    ('Yasmine'),
    ('Zane'),
    ('Abby'),
    ('Ben'),
    ('Cathy'),
    ('Dylan'),
    ('Elle'),
    ('Finn'),
    ('Giselle'),
    ('Hank'),
    ('Isabel'),
    ('Jake'),
    ('Katie');

-- insert the names into the CLIENT table
INSERT INTO CLIENT (GENDER, NAME, AGE, FAVORITE_FOOD)
SELECT
    CASE WHEN random() < 0.5 THEN 'M' ELSE 'F' END, -- randomly assign gender
    name,
    floor(random() * 50) + 20, -- random age between 20 and 70
    CASE WHEN random() < 0.5 THEN 'Pizza' ELSE 'Spaghetti' END -- assign random food between 2
FROM temp_names;

-- drop the temporary table used to set the names (might use it on next sprint)
DROP TABLE temp_names;
    


-- inserting food with the most common allergies
INSERT INTO FOOD_ALLERGY (FOOD) VALUES
    ('Peanuts'),
    ('Shellfish'),
    ('Lactose'),
    ('Gluten'),
    ('Soy'),
    ('Eggs'),
    ('Tree Nuts'),
    ('Fish'),
    ('Milk'),
    ('Wheat');
    

-- generation random relatioships between people and allergies
INSERT INTO CLIENT_ALLERGY (CLIENT_ID, ALLERGY_ID) VALUES
    (1, 1), -- Marcos is allergic to Peanuts
    (2, 2), -- Giovanni is allergic to Shellfish
    (3, 3), -- Allice is allergic to Lactose
    (1, 5),
    (4, 4),
    (5, 5),
    (2, 8),
    (6, 6),
    (3, 9),
    (7, 7),
    (4, 8),
    (8, 8),
    (2, 9),
    (5, 9),
    (9, 9),
    (3, 6),
    (10, 6),
    (8, 7);



SELECT * FROM CLIENT

--REGULAR customer
CREATE TABLE IF NOT EXISTS customer

(
    id serial PRIMARY KEY, 
    gender VARCHAR(1) NOT NULL,
    name VARCHAR(40) NOT NULL,
    age INTEGER NOT NULL,
    check(GENDER in ('M', 'F'))
);

CREATE TABLE IF NOT EXISTS address
(
    id serial PRIMARY KEY, 
    customer_id integer REFERENCES customer (id) ,
    street VARCHAR(30) NOT NULL,
    street_number INTEGER NOT NULL,
    zipCode VARCHAR(10) NOT NULL ,
    city VARCHAR(30) NOT NULL,
    province VARCHAR(30) NOT NULL,
    country   VARCHAR(30) NOT NULL,
    isDelivery BOOLEAN,
    isPayment  BOOLEAN
);


CREATE TABLE IF NOT EXISTS orders
(
    id serial PRIMARY KEY, 
    customer_id integer REFERENCES customer (id) ,
    order_id VARCHAR(30) NOT NULL,
    title VARCHAR(30) NOT NULL,
    total_amount INTEGER NOT NULL,
    restaurant VARCHAR(30) NOT NULL,
    paid  BOOLEAN
);


INSERT INTO customer
 (gender, name, age) VALUES('M', 'MARCOS PETRUCCI', 23);
INSERT INTO customer
 (gender, name, age) VALUES('M', 'GIOVANNI A', 23);

 INSERT INTO customer
 (gender, name, age) VALUES('M', 'Mario Rossi', 23);

 INSERT INTO address 
 (customer_id,street,street_number,zipCode,city,province,country,isDelivery,isPayment) VALUES('1','Via del Corso','3','00118','Roma','RM','Italy',true,false);

 INSERT INTO address 
 (customer_id,street,street_number,zipCode,city,province,country,isDelivery,isPayment) VALUES('2','Via Dante','4','20121','Milano','MI','Italy',true,true);

 INSERT INTO address 
 (customer_id,street,street_number,zipCode,city,province,country,isDelivery,isPayment) VALUES('3','Via della Pace','10','62029','Tolentino','MC','Italy',true,false);


INSERT INTO orders 
 (customer_id,order_id,title,total_amount,restaurant,paid) VALUES('1','#1001','My first order',35,'Pizzeria La Capricciosa',true);
 INSERT INTO orders 
 (customer_id,order_id,title,total_amount,restaurant,paid) VALUES('2','#1002','My first order',50,'Sumo sushi',false);
 INSERT INTO orders 
 (customer_id,order_id,title,total_amount,restaurant,paid) VALUES('3','#1003','My first order',32,'Ristorante Il Vecchio Molino',true);







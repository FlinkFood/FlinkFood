# Demo
This file is intended to give an overview of how to demo the FlinkFood application


# Before starting the demo

Before starting the demo make sure you have started the application. This is smart since during the demo, you might experience issues with processes taking longer than usual since we are streaming the demo online. To start everything up, do this:
```bash
docker compose up -d --build
```

## Setting up the Apache Flink jobs 
Luckily this is done by docker automatically. After waiting a while visit [Apache Flink Web Dashboard](http://localhost:8081/#/overview) and make sure that it has three running jobs (surviving for more than 1 minute).

Go to your preferred mongoDB discovery view. I personally use:[MongoDB Compass | MongoDB](https://www.mongodb.com/products/tools/compass). Connect to: mongodb://localhost:27017.

Currently there should be 3 views in the Flinkfood database:

-   restaurant_views
-   user_sink
-   dish_views

## Setting up the metrics dashboard
We are using Grafana as a metrics dashboard which is connected to the Flink internal metrics system. There is some setup needed after starting the docker container to make the dashboard work. The steps to do this can be found here: [The documentation for setting up Metrics with Grafana and Prometheus](./metrics.md). **Please follow the steps in this file before going ahead with the demo as it is important for the demo.**

After making sure all of this works, you should be able to go ahead with your demonstration.

## NOTE: Where to run SQL commands
Make sure you are in the postgre container when running the commands:
```bash
docker exec -it postgres psql -U postgres -d flinkfood
```

# Demonstration
The point of this demonstration is to show how the Flink Engine can be used to continouslly process data from a set of databases.The demo will show how the CDC system Debezium captures changes in the data sources and sends these through Kafka to the FlinkEngine. In the end the processed data in the FlinkEngine will be stored in single views in mongoDB.

## Introduction - Putting things into a context

### The input databases

First of all, we want to show that this are the different databases the Flinkfood project fetches data from and creates single views out of

```sql
\dt
```

### Example restaurant - Da mario

This is an example on how a table the Flinkfood project addresses looks like:

```sql
SELECT * FROM restaurant_info WHERE name='Ristorante da Mario';
```

The services the restaurant provides:

```sql
SELECT * FROM restaurant_service WHERE restaurant_id=1;
```

The address of the restaurant:

```sql
SELECT * FROM restaurant_address WHERE restaurant_id=1;
```

### Example jobs.

If we now go to [Apache Flink Web Dashboard](http://localhost:8081/#/overview) we can see that there are 3 different jobs running. These correspond to the aggreation of each of the views. 
Now we will click on the job corresponding to the RestaurantViewJob. This gives an overview of all the different steps required to create the single view. This encompasses getting data from Kafka, joining and altering table and in the end sending them to MongoDB. To summarize this dashboard can make it easier for customers and the Flinkfood team to visualize what is going on. Since you get an overview of the system and load on different parts of the job, the dashboard also makes it easier to debug and optimize our jobs.

In the rest of the demo we will see examples of usages for these jobs.

### Metrics
Since the FlinkFood project is meant to give insights for the MIA platform team to how Flink performs it could be useful to get more detailed information about the jobs being done than what is shown in the Flink Dashboard. For this reason, a dashboard showing a lot of different statistics have been developed. This dashboard can be reached through [this link](http://localhost:3000). Login with the following credentials:
```plaintext
username: admin
password: flink
```
Here we show the different charts. The most interesting charts can be found under the dropdown: `"Job Manager (Slots & Jobs)"`.

### Example single view.
Go to your MongoDB visualizer and show an example of how the different single views look. Here we can emphesize that the single view contains data aggregated from a lot of different views.

## Interacting with the system
In the rest of the demo, we have set up different "stories" which will show how users interact with the system and what the system does. 
### Story 1 -  The restaurant owner

**Scenario**: You are a restaurant owner of Ristorante da Mario, and need your to change your buisness email addres for you restaurant. You would like this change to also be visible for the flinkfood users. You follow these steps:

1.  Update the Restaurant email address:
    1.  Show that the aggregation is happening in the running job in the Apache Flink dashboard
    2.  Show the new entry in the single view.
        ```sql
        UPDATE restaurant_info SET email='mario@gmail.com' WHERE id = 1;
        ```

**Scenario 2:** The restaurant is struggling with getting enough customers. To deal with this you decide to add a new dish to be served by your restaurant:
1. Add new dish to the table of dishes:
```sql
INSERT INTO dish (id, restaurant_id, name, price, currency, category, description)
VALUES (100, 1, 'Pizza Margarita', 10, 'EUR', 'Second Course', 'Delicous Pizza with cheese')
;
```
2. Show that the dish is added under 'Served Dishes' in "Ristorante Di Mario" in the restaurant view.
        
### Story 2 - The customer

**Scenario**: You are the customer of the FlinkFood service. This is your information on the service:

```sql 
SELECT * FROM customer WHERE id=1;
```
You just logged into FlinkFood and see that Ristorante Di Mario has a new dish. The legendary Pizza Margarita. You decide to order it:

1. Add new order to the order table:
```sql 
INSERT INTO public.order (id, name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES (11, 'ord-987654321-AAAA', 1, 1, 3, '2023-12-03', '2023-12-03', '2023-12-03', 'Pizza Margarita', 10, 'EUR', 'f');
```
2. Go to the users_sink view and show that the user Mario Rossi has an order for Pizza Margarita in his singleview.

You are very satisfied with the pizza and want to leave an good review:
1. Add review to reviews_dish table:
```sql
INSERT INTO reviews_dish (id, dish_id, customer_id, rating, description) VALUES (100, 100, 1, 5, 'Great Pizza. Mario is a great chef!');
```
2. Go to the user_sink view. Find User Mario Rossi with ID=1. Show that Mario Rossi has the review under 'dish review'
3. Go to the dish view. Find dish with id 100 ("Pizza Margarita")
4. Show that the review is placed under "review".


# Table of Contents

1.  [Before starting the demo](#org317d684):ATTACH:
2.  [Demonstration](#orgca7d5b0)
    1.  [Introduction - Putting things into a context](#org0dcbb46)
        1.  [The input databases](#org4974d61)
        2.  [Example restaurant - Da mario](#orga370746)
        3.  [Example jobs.](#orge3bf807)
        4.  [Example single view.](#org4bf7276)
    2.  [The restaurant owner](#org64e7f11)
    3.  [The customer](#org1884158)

This file is intended to give an overview of how to demo the FlinkFood application


# Before starting the demo 

Before starting the demo make sure you have started the application. This is smart since during the demo, you might experience issues with processes taking longer than usual since we are streaming the demo online. To start everything up, do this:
```sql
docker compose up -d --build
```

After waiting a while visit [Apache Flink Web Dashboard](http://localhost:8081/#/overview) and make sure that it has three running jobs (surviving for more than 1 minute).

Go to your preferred mongoDB discovery view. I personally use:[MongoDB Compass | MongoDB](https://www.mongodb.com/products/tools/compass). Connect to: mongodb://localhost:27017.

Currently there should be 3 views in the Flinkfood database:

-   restaurants<sub>view</sub>
-   users<sub>sink</sub>

Currently a restaurant<sub>view</sub> should look like this:
```json
{
  "ID": 1,
  "name": "Ristorante da Mario",
  "province": "RM",
  "vatCode": 2345678,
  "email": "mario.gmail.com",
  "accessibleEntrance": true,
  "address": {
    "street": "Via Roma",
    "number": "1",
    "zipCode": 12345,
    "city": "Roma",
    "country": "Italy"
  },
  "status": {
    "takeAway": true,
    "delivery": true,
    "dineIn": true
  },
  "services": {
    "parkingLots": 10,
    "childrenArea": true,
    "childrenFood": true
  },
  "reviews": []
}
```

After making sure all of this works, you should be able to go ahead with your demonstration.

## NOTE: Where to run SQL commands
Make sure you are in the postgre container when running the commands:
```sh
docker exec -it postgre bash
```

# Demonstration

The point of this demonstration is to show that our Flink engine is dynamically listening to data changes in a set of databases. It then takes this new data, puts it together into single views and stores it in a final database. This new database is visible for the Flinkfood users


<a id="org0dcbb46"></a>

## Introduction - Putting things into a context


<a id="org4974d61"></a>

### The input databases

First of all, we want to show that this are the different databases the Flinkfood project fetches datafrom and creates single views out of

```sql
\dt
```


<a id="orga370746"></a>

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


<a id="orge3bf807"></a>

### Example jobs.

Visit [Apache Flink Web Dashboard](http://localhost:8081/#/overview) and show the different jobs running.
Go to the RestaurantViewJob and show the overview. On the left in the overview you can see our sources for the integration. This makes it easier for customers and the Flinkfood team to visualize what is going on. You can also see how the JOIN operations are done in the pipeline. Making it easier to debug.

In the rest of the demo we will see examples of usages for these jobs.


<a id="org4bf7276"></a>

### Example single view.

Go to your MongoDB visuzalizer and show an example of how the Restaurant view looks before the first aggregations. Here we can emphesize that there is more data in this table then the first table (address, services).


<a id="org64e7f11"></a>

## The restaurant owner

**Scenario**: You are a restaurant owner of Ristorante da Mario, and need your to change your buisness email addres for you restaurant. You would like this change to also be visible for the flinkfood users. You follow these steps:

1.  Update the Restaurant email address:
    1.  Show that the aggregation is happening in the running job in the Apache Flink dashboard
    2.  Show the new entry in the single view.

    UPDATE restaurant_info SET email='mario@gmail.com' WHERE id = 1;


<a id="org1884158"></a>

## The customer

**Scenario**: You are the customer of the FlinkFood service. This is your information on the service:

```sql 
SELECT * FROM customer WHERE id=1;
```

Now, you are buying a dish of Pizza Margarita from your good friend, the owner of Ristorante da Mario.

```sql 
INSERT INTO public.order (id, name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order)
VALUES
(11, 'ord-987654321-AAAA', 1, 1, 3, '2023-12-03', '2023-12-03', '2023-12-03', 'Pizza Margarita', 15, 'USD', 'f');
```

Now you are so happy since you can see the change in the user single view (Show mongoDB)

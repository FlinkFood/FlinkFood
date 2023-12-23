# Implementation notes
This is the way I firstly implemented the aggregation. It's the FLinkSQL way
``` Java
        // declaration of the table view to be sinked. I want to wwitch to use the one above in the future
        rEnv.gettEnv()
                .executeSql(
                "CREATE TABLE restaurant_view "+
                        "(restaurant_id INT, "+
                        "dishes " +
                        "ARRAY<ROW<" +
                        "id BIGINT," +
                        "restaurant_id BIGINT," +
                        "dish_name STRING," +
                        "price INT," +
                        "currency STRING," +
                        "category STRING," +
                        "description STRING>>," +
                        "PRIMARY KEY (restaurant_id) NOT ENFORCED) " +
                        "WITH ('connector' = 'mongodb', 'uri' = 'mongodb://localhost:27017'," +
                        "'database' = 'flinkfood'," +
                        "'collection' = 'restaurants_view')");


        // this command submits the function to the SQL engine
        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.supportClasses.ArrayAggr'");


        // aggregation is created with the insert statement, adding the data into the restaurant_view table.
        // maybe it's possible to use just an intermediary view.
        var stmtSet = rEnv.gettEnv().createStatementSet();
        stmtSet.addInsertSql(
                "INSERT INTO restaurant_view " +
                "SELECT restaurant_id, " +
                    // here would be nice to have a modular way to add the tables to the view
                    "ARRAY_AGGR(ROW(id, restaurant_id, name, price, currency, category, description)) " +
                    "FROM dish " +
                    "GROUP BY restaurant_id ")
                .execute();
```
and the result is I obtain:
```JSON
[
  {
    "_id": 1,
    "dishes": [
      {
        "id": 1,
        "restaurant_id": 1,
        "dish_name": "Spaghetti alla carbonara",
        "price": 10,
        "currency": "EUR",
        "category": "first course",
        "description": "Spaghetti with eggs, guanciale and pecorino cheese"
      },
      {
        "id": 2,
        "restaurant_id": 1,
        "dish_name": "Saltimbocca alla romana",
        "price": 15,
        "currency": "EUR",
        "category": "second course",
        "description": "Veal with prosciutto and sage"
      },
      ...
    ],
    "restaurant_id": 1
  },
...
]
```

## 2.0
This other way it's more elegant
```Java
        rEnv.gettEnv().from("dish")
                                .groupBy($("restaurant_id"))
                                .aggregate(call(ArrayAggr.class, Row.of($("*"))))
                                .select($("*"))
                .insertInto("restaurant_view")
                .execute();
```
but does not appear to work.

## go into detail in the FLINK SQL
The generated SVTable schema is the following: (e.g. as of 23-12-23, it may change)
```SQL
CREATE TABLE view (view_key INT primary key not enforced,
                   restaurant_service ARRAY<ROW<restaurant_id INT ,
                                               take_away BOOLEAN,
                                               delivery BOOLEAN,
                                               dine_in BOOLEAN,
                                               parking_lots INT,
                                               accessible BOOLEAN,
                                               children_area BOOLEAN,
                                               children_food BOOLEAN >>,
                   restaurant_address ARRAY<ROW<   restaurant_id INT ,
                                                   street VARCHAR(255),
                                                   address_number VARCHAR(10),
                                                   zip_code INT,
                                                   city VARCHAR(255),
                                                   province VARCHAR(2),
                                                   country VARCHAR(255) >>,
                   restaurant_review ARRAY<ROW<id INT,
                                               restaurant_id INT,
                                               customer_id INT,
                                               rating INT,
                                               commentary VARCHAR(255) >>,
                   dish ARRAY<ROW< id INT,
                                   restaurant_id INT,
                                   name VARCHAR(255),
                                   price INT,
                                   currency VARCHAR(3),
                                   category VARCHAR(255),
                                   description VARCHAR(255) >>
                ) 
    WITH ('connector' = 'mongodb','uri' = 'mongodb://localhost:27017','database' = 'flinkfood','collection' = 'restaurants_view' )
```


Previously I was using the following query:
```SQL
INSERT INTO view SELECT ((SELECT restaurant_id FROM restaurant_service),
(SELECT ARRAY_AGGR(ROW(restaurant_id,take_away,delivery,dine_in,parking_lots,accessible,children_area,children_food)) FROM restaurant_service GROUP BY restaurant_id),
(SELECT ARRAY_AGGR(ROW(restaurant_id,street,address_number,zip_code,city,province,country)) FROM restaurant_address GROUP BY restaurant_id),
(SELECT ARRAY_AGGR(ROW(id,restaurant_id,customer_id,rating,commentary)) FROM restaurant_review GROUP BY restaurant_id),
(SELECT ARRAY_AGGR(ROW(id,restaurant_id,name,price,currency,category,description)) FROM dish GROUP BY restaurant_id))
```
This raises the following error:
``` Flink
doesn't work but i got a very cool error message:
FlinkLogicalSink(table=[default_catalog.default_database.view], fields=[restaurant_id, $f0, $f00, $f01, $f02])
+- FlinkLogicalJoin(condition=[true], joinType=[left])
:- FlinkLogicalJoin(condition=[true], joinType=[left])
:  :- FlinkLogicalJoin(condition=[true], joinType=[left])
:  :  :- FlinkLogicalJoin(condition=[true], joinType=[left])
:  :  :  :- FlinkLogicalCalc(select=[restaurant_id])
:  :  :  :  +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_service]], fields=[restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food])
:  :  :  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
:  :  :     +- FlinkLogicalCalc(select=[EXPR$0])
:  :  :        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
:  :  :           +- FlinkLogicalCalc(select=[restaurant_id, ROW(restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food) AS $f1])
:  :  :              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_service]], fields=[restaurant_id, take_away, delivery, dine_in, parking_lots, accessible, children_area, children_food])
:  :  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
:  :     +- FlinkLogicalCalc(select=[EXPR$0])
:  :        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
:  :           +- FlinkLogicalCalc(select=[restaurant_id, ROW(restaurant_id, street, address_number, zip_code, city, province, country) AS $f1])
:  :              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_address]], fields=[restaurant_id, street, address_number, zip_code, city, province, country])
:  +- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
:     +- FlinkLogicalCalc(select=[EXPR$0])
:        +- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
:           +- FlinkLogicalCalc(select=[restaurant_id, ROW(id, restaurant_id, customer_id, rating, commentary) AS $f1])
:              +- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, restaurant_review]], fields=[id, restaurant_id, customer_id, rating, commentary])
+- FlinkLogicalAggregate(group=[{}], agg#0=[SINGLE_VALUE($0)])
+- FlinkLogicalCalc(select=[EXPR$0])
+- FlinkLogicalAggregate(group=[{0}], EXPR$0=[ARRAY_AGGR($1)])
+- FlinkLogicalCalc(select=[restaurant_id, ROW(id, restaurant_id, name, price, currency, category, description) AS $f1])
+- FlinkLogicalTableSourceScan(table=[[default_catalog, default_database, dish]], fields=[id, restaurant_id, name, price, currency, category, description])

SINGLE_VALUE aggregate function doesn't support type 'ARRAY'.
```
Da chatGPT:
The error message you're encountering, TableException: Sql optimization: Cannot generate a valid execution plan for the given query: FlinkLogicalSink(table=[...]) SINGLE_VALUE aggregate function doesn't support type 'ARRAY', indicates that there is an issue with how you're using an aggregate function, specifically SINGLE_VALUE, with an array type in Apache Flink.
The SINGLE_VALUE aggregate function in Flink is designed to return a single value from a group of values. However, it seems you're trying to use it on an array type, which is not supported.
To resolve this issue, consider the following steps:
Review Aggregate Function Usage: Check the part of your query where you are using the SINGLE_VALUE aggregate function. This function is typically used in scenarios where you are certain that a group of values contains exactly one unique value and you want to retrieve that value. Ensure that its usage is appropriate for your data and your query's logic.
Type Compatibility: If you need to aggregate or manipulate array types, you'll need to use functions or operations that are specifically designed for array types. Flink provides various functions to work with complex types like arrays, maps, and rows.
Alternative Aggregation Strategy: If your goal is to aggregate data within each array, you might need to rethink your aggregation strategy. For example, if you need to summarize or transform data inside each array, consider using functions like COLLECT or REDUCE.
Flatten Arrays: If your use case permits, you could flatten the arrays before applying the aggregation. This would involve transforming each element of the array into a separate row, which can then be aggregated using standard functions.
Review Schema and Query: Ensure that your query's logic and the schemas of the tables you're working with align correctly. Specifically, make sure that the types and structures of the fields in your query match those expected by the SINGLE_VALUE function and the target sink.
Consult Documentation: Review the Apache Flink documentation for any specific requirements or limitations regarding the use of aggregate functions with complex data types like arrays.
If these steps do not resolve your issue, providing more details about your specific query and how you're using the SINGLE_VALUE function within it would be helpful for further diagnosis and assistance.

20-12-2023
Today I found another way to join tables that for whatever reason works
I now switched to: This is the query model that is created in the [RestaurantView.java](flinkjobs%2FRestaurantView.java) does in the `createInsertSVQuery` method.
```SQL
INSERT INTO view
SELECT 
    rs.restaurant_id,
    ARRAY_AGGR(ROW(rs.restaurant_id, rs.take_away, rs.delivery, rs.dine_in, rs.parking_lots, rs.accessible, rs.children_area, rs.children_food)),
    ARRAY_AGGR(ROW(ra.restaurant_id, ra.street, ra.address_number, ra.zip_code, ra.city, ra.province, ra.country)),
    ARRAY_AGGR(ROW(rr.id, rr.restaurant_id, rr.customer_id, rr.rating, rr.commentary)),
    ARRAY_AGGR(ROW(d.id, d.restaurant_id, d.name, d.price, d.currency, d.category, d.description))
FROM 
    restaurant_service rs
LEFT JOIN 
    restaurant_address ra ON rs.restaurant_id = ra.restaurant_id
LEFT JOIN 
    restaurant_review rr ON rs.restaurant_id = rr.restaurant_id
LEFT JOIN 
    dish d ON rs.restaurant_id = d.restaurant_id
GROUP BY 
    rs.restaurant_id;
```
This will need a little bit more of work because of the "references" to the tables, but it's a lot more readable and it works.


21-12-2023
# Problems
*order* is not a good name for a table, because it's a reserved word in SQL. I changed it to *orderx*
*comment* is not a good name for a field, because it's a reserved word in SQL. I changed it to *commentary*, but this is a problem
in the kafka connector (apartently)
*DATE* is not a good type in FlinkSQL, I tried to change it to TIMESTAMP(3)

22-12-2023
for some reason there are a lot of replicas in the final table. I need to investigate why
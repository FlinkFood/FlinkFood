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
        rEnv.gettEnv().executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.ArrayAggr'");


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


## Error
`SINGLE_VALUE aggregate function doesn't support type 'ARRAY'.`
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
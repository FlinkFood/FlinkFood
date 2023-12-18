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
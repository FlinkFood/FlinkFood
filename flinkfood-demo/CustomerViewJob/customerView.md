# customerView Test

1. First make sure all the containers are correctly reset:

```bash
 docker compose down -v
```

2. Now, run all the docker containers:

```bash
docker compose up -d
```

This will run the containers in the background.

3. After the containers are properly started, we create the relevant kafka topics and configure the debezium connector by running the following command:

```bash
../startup_v2.sh
```

In this regard I have found out that in order let Flink captures the events correctly we can eliminate some of the verbosity generated by Debezium, but we can't just extract the json object as we are doing right now. So in order to do that we have to keep the before/after parts of the payload as following:

```json
   "before": null,
   "after": {
      "id": 1,
      "name": "ord-324567879-RAAC",
      "customer_id": 1,
      "restaurant_id": 1,
      "supplier_id": 2,
      "order_date": 18748,
      "payment_date": 18748,
      "delivery_date": 18748,
      "description": "Spaghetti alla carbonara",
      "total_amount": 10,
      "currency": "EUR",
      "supply_order": false
   },
   "source": {
      "version": "1.9.7.Final",
      "connector": "postgresql",
      "name": "postgres",
      "ts_ms": 1703678098684,
      "snapshot": "true",
      "db": "flinkfood",
      "sequence": "[null,\"28038520\"]",
      "schema": "public",
      "table": "order",
      "txId": 791,
      "lsn": 28038520,
      "xmin": null
   },
   "op": "r",
   "ts_ms": 1703678099239,
   "transaction": null
}
```

So I have modified the connector configuration in this way:

```bash
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "schema.whitelist": "public",
        "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
        "key.converter":"org.apache.kafka.connect.json.JsonConverter",
        "key.converter.schemas.enable":false,
        "value.converter":"org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable":false,
        "schemas.enable":false
    }
}'

```

By removing the `"transforms": "unwrap",` line.

**NOTE:** Wait a couple of minutes for the initialization of all the kafka topics.
You can check the creation of topics by going to [kafdrop dashboard](http://localhost:9000)

4. Start the flink job by running the `CustomerViewJob.java`.

## Code explanation

**NOTE:** Every string inserted into a .executeSql() method means we are using FlinkSQL, but there should be an equivalent java method for every statement in TableAPI. At the moment seems an easier option.

1. Set all the configuration needed: StreamExecutionEnvironment and StreamTableEnvironment environments, mini-batch mode and register the aggregation function.

```java
   // Setting up Flink execution environment
                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
                TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "500 ms");
                tableConfig.set("table.exec.mini-batch.size", "1000");
                tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");
```

2. At this point it is possible to register the tables in the Flink SQL Catalog :
   **NOTE:** Unlike the previous version now I am specifying the `'format' = 'debezium-json' ` format.

```java
tableEnv.executeSql("CREATE TABLE Customer (\r\n" + //
                                "  id INT,\r\n" + //
                                "  username STRING,\r\n" + //
                                "  first_name STRING,\r\n" + //
                                "  last_name STRING,\r\n" + //
                                "  birthdate STRING,\r\n" + //
                                "  email STRING,\r\n" + //
                                "  fiscal_code STRING,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.customer',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

                tableEnv.executeSql("CREATE TABLE Orders (\r\n" + //
                                "  id INT,\r\n" + //
                                "  name STRING,\r\n" + //
                                "  customer_id INT,\r\n" + //
                                "  restaurant_id INT,\r\n" + //
                                "  supplier_id INT,\r\n" + //
                                "  order_date STRING,\r\n" + //
                                "  payment_date STRING,\r\n" + //
                                "  delivery_date STRING,\r\n" + //
                                "  description STRING,\r\n" + //
                                "  total_amount INT,\r\n" + //
                                "  currency STRING,\r\n" + //
                                "  supply_order BOOLEAN,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "  'connector' = 'kafka',\r\n" + //
                                "  'topic' = 'postgres.public.order',\r\n" + //
                                "  'properties.bootstrap.servers' = 'localhost:9092',\r\n" + //
                                "  'properties.group.id' = 'testGroup', \r\n" + //
                                "  'scan.startup.mode' = 'earliest-offset',\r\n" + //
                                "  'format' = 'debezium-json'\r\n" + //
                                ");");

```

3. After that we can create a table for the single view:
   **VERY IMPORTANT:** We cand define a table sink directly in the table registration. So at every event the table will be automatically be sinked into MongoDB without have to use es.`RestaurantRowToBsonDocument.java` or specify any deserialization function. [Table connectors](https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/connectors/table/overview/`).

```java
   tableEnv.executeSql("CREATE TABLE CustomeView (\r\n" + //
                                "  id INT,\r\n" + //
                                "  first_name STRING,\r\n" + //
                                "  last_name STRING,\r\n" + //
                                "  orders ARRAY<row<id INT, name STRING, description STRING>>,\r\n" + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "   'connector' = 'mongodb',\r\n" + //
                                "   'uri' = 'mongodb://localhost:27017',\r\n" + //
                                "   'database' = 'flinkfood',\r\n" + //
                                "   'collection' = 'users_sink'\r\n" + //
                                ");");
```

4. Finally the aggregation query can be runned and the results can be checked in MongoDB:

```java
    tableEnv.executeSql(
                                "INSERT INTO CustomeView SELECT DISTINCT  c.id,c.first_name,c.last_name,(SELECT ARRAY_AGGR(ROW(o.id,o.name,o.description)) FROM Orders o WHERE o.customer_id = c.id) FROM Customer c;");
```

5. I tested it with the ADD, UPDATE and DELETE cases and seems working good.

```json
{
  "_id": 1,
  "first_name": "Mario",
  "id": 1,
  "last_name": "Rossi",
  "orders": [
    {
      "id": 1,
      "name": "ord-324567879-RAAC",
      "description": "Spaghetti alla carbonara"
    },
    {
      "id": 6,
      "name": "ord-987654321-THTD",
      "description": "Teriyaki Chicken Bento"
    }
  ]
}
```
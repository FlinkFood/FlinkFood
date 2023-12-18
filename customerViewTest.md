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

3. Wait for some seconds to make sure the containers are properly started. Then we want to create the relevant kafka topics and kafka connectors to debezium. This is done by running the following command:

```bash
../startup.sh
```

**NOTE:** Wait a couple of minutes for the initialization of all the kafka topics.
You can check the creation of topics by going to [kafdrop dashboard](http://localhost:9000`)

4. Start the flink job by running the `CustomerViewJob_v2.java`.

## Code explanation

**NOTE:** Every string inserted into a .executeSql() method means we are using FlinkSQL, but there should be an equivalent java method for every statement in TableAPI. At the moment seems an easier option.

1. Creating the kafka sources one for each topic/table to include in the view:

```java
 KafkaSource<Customer> sourceCustomer = KafkaSource.<Customer>builder()
                                .setBootstrapServers(KAFKA_URI)
                                .setTopics(SOURCE_CUSTOMER_TABLE)
                                .setGroupId("my-group")
                                .setStartingOffsets(OffsetsInitializer.earliest())
                                .setValueOnlyDeserializer(new KafkaCustomerSchema())
                                .build();

                                //....

```

2. Set the StreamExecutionEnvironment and StreamTableEnvironment environments:

```java
  StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
  StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
```

3. Set the mini-batch configuration (it basically avoids the creation of mutliple separate events for every insert and thus having duplicates in the query result). Link to [Aggregation function guide](https://www.youtube.com/watch?v=ICJ7-YyaC-4`) for more:

```java
   TableConfig tableConfig = tableEnv.getConfig();
                tableConfig.set("table.exec.mini-batch.enabled", "true");
                tableConfig.set("table.exec.mini-batch.allow-latency", "5 s");
                tableConfig.set("table.exec.mini-batch.size", "5000");
```

4. Creating DataStreams from the kafka sources, converting them into tables and creating the corresponding Views:

```java
    DataStream<Customer> streamCustomer = env
                                .fromSource(sourceCustomer, WatermarkStrategy.noWatermarks(), "Kafka Source")
                                .keyBy(Customer::getId);

                Table customerTable = tableEnv.fromDataStream(streamCustomer);

                tableEnv.createTemporaryView("Customer", tableEnv.toChangelogStream(customerTable));

                //....
```

5. Register the custom array aggregation function defined in the `ArrayAggr.java` file:

```java
  tableEnv.executeSql("CREATE FUNCTION ARRAY_AGGR AS 'org.flinkfood.flinkjobs.ArrayAggr';");
```

6. First query method tried following the [Aggregation function guide](https://www.youtube.com/watch?v=ICJ7-YyaC-4`). The query works but returns an object of type Row that need to be converted somehow into a json object in order to be sinked into MongoDB:

```java
  Table resultTable4 = tableEnv.sqlQuery(
                                "SELECT DISTINCT c.id,c.first_name,c.last_name,(SELECT ARRAY_AGGR(ROW(o.id,o.name,o.description)) FROM Orders o WHERE o.customer_id = c.id) FROM Customer c;");

                 tableEnv.toChangelogStream(resultTable4).print();
```

7. Query result:

```bash
 2> +I[3, Alice, Smith, [+I[3, ord-543210987-MXFT, Tacos Fiesta Platter], +I[8, ord-345678901-FRST, Fajitas Fiesta]]]
6> +I[1, Mario, Rossi, [+I[1, ord-324567879-RAAC, Spaghetti alla carbonara], +I[6, ord-987654321-THTD, Teriyaki Chicken Bento]]]
3> +I[4, Harry, Potter, [+I[4, ord-654321098-MDMD, Mediterranean Feast], +I[9, ord-876543210-PPSS, Prosciutto and Pesto Sandwich]]]
3> +I[2, John, Doe, [+I[2, ord-789012345-SSJT, Sushi Combo Deluxe], +I[7, ord-234567890-QCTC, Quinoa Chicken Salad]]]
3> +I[5, Alex, Johnson, [+I[5, ord-123456789-VEGB, Vegetarian Delight], +I[10, ord-567890123-VGPI, Vegan Pizza]]]
```

8. Second query method tried inserting the previous query in the JSON_OBJECT() function to convert into json see the [Documentation](https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/dev/table/functions/systemfunctions/#json-functions). And substituting the ROW() function inside the ARRAY_AGGR() function with the JSON_OBJECT(), but this seems to nullify the purpose of the custom function itself.

```java
 Table resultTable3 = tableEnv
                                .sqlQuery("SELECT DISTINCT JSON_OBJECT('customer_id' VALUE c.id, 'first_name' VALUE c.first_name, 'last_name' VALUE c.last_name, \n"
                                                +
                                                " 'orders' VALUE ARRAY_AGGR(JSON_OBJECT('order_id' VALUE o.id, 'name' VALUE o.name, 'description' VALUE o.description)))as customer_view FROM Customer c  INNER JOIN  Orders o ON o.customer_id = c.id GROUP BY c.id, c.first_name, c.last_name;");
```

9. Query result(this query is also working but the aggregated array is still detected as a json string instead of json object):

```bash
7> +I[{"customer_id":3,"first_name":"Alice","last_name":"Smith","orders":["{\"description\":\"Tacos Fiesta Platter\",\"name\":\"ord-543210987-MXFT\",\"order_id\":3}","{\"description\":\"Fajitas Fiesta\",\"name\":\"ord-345678901-FRST\",\"order_id\":8}"]}]
5> +I[{"customer_id":1,"first_name":"Mario","last_name":"Rossi","orders":["{\"description\":\"Spaghetti alla carbonara\",\"name\":\"ord-324567879-RAAC\",\"order_id\":1}","{\"description\":\"Teriyaki Chicken Bento\",\"name\":\"ord-987654321-THTD\",\"order_id\":6}"]}]
7> +I[{"customer_id":4,"first_name":"Harry","last_name":"Potter","orders":["{\"description\":\"Mediterranean Feast\",\"name\":\"ord-654321098-MDMD\",\"order_id\":4}","{\"description\":\"Prosciutto and Pesto Sandwich\",\"name\":\"ord-876543210-PPSS\",\"order_id\":9}"]}]
8> +I[{"customer_id":2,"first_name":"John","last_name":"Doe","orders":["{\"description\":\"Sushi Combo Deluxe\",\"name\":\"ord-789012345-SSJT\",\"order_id\":2}","{\"description\":\"Quinoa Chicken Salad\",\"name\":\"ord-234567890-QCTC\",\"order_id\":7}"]}]
8> +I[{"customer_id":5,"first_name":"Alex","last_name":"Johnson","orders":["{\"description\":\"Vegetarian Delight\",\"name\":\"ord-123456789-VEGB\",\"order_id\":5}","{\"description\":\"Vegan Pizza\",\"name\":\"ord-567890123-VGPI\",\"order_id\":10}"]}]
```

10. From this result we can sink it into MongoDB(as you can see the orders are inserted as strings):

```json
{
  "_id": {
    "$oid": "65804175bd2200a4621a58f6"
  },
  "customer_id": 2,
  "first_name": "John",
  "last_name": "Doe",
  "orders": [
    "{\"description\":\"Sushi Combo Deluxe\",\"name\":\"ord-789012345-SSJT\",\"order_id\":2}",
    "{\"description\":\"Quinoa Chicken Salad\",\"name\":\"ord-234567890-QCTC\",\"order_id\":7}"
  ]
}
```

11. Am using the first version of the mongodb sink without the update of the already existing documents(the commented part) because it's not working:

```java
 MongoSink<String> sink = MongoSink.<String>builder()
                                .setUri(MONGODB_URI)
                                .setDatabase(SINK_DB)
                                .setCollection(SINK_DB_TABLE)
                                .setBatchSize(1000)
                                .setBatchIntervalMs(1000)
                                .setMaxRetries(3)
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)

                                .setSerializationSchema(
                                                (input, context) -> new InsertOneModel<>(BsonDocument.parse(input)))

                                /*
                                 * .setSerializationSchema((input, context) -> {
                                 * BsonDocument document = BsonDocument.parse(input);
                                 * int idValue = document.getInt32(new String("customer_id")).getValue();
                                 * Bson filter = Filters.eq("customer.id", idValue);
                                 * return new ReplaceOneModel<>(filter, document,
                                 * new ReplaceOptions().upsert(true));
                                 * })
                                 */

                                .build();
```

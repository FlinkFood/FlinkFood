#!/bin/bash

# Set environment variables
export KAFKA_URI=kafka:29092
export MONGODB_SERVER=mongodb://mongo:27017

echo "Starting jobmanager..."
/opt/flink/bin/jobmanager.sh start

# Wait until the Kafka Connect service is available
while ! curl -s 'http://kconnect:8083/connectors' > /dev/null; do
 echo "Waiting for Kafka Connect to be available..."
 sleep 5
done

echo "Kafka Connect is now available. Sending Kafka Connectors to Kafka Connect..."

curl -X POST 'http://kconnect:8083/connectors' -H 'Content-Type: application/json' -d '{
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
       "transforms": "unwrap",
       "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
       "key.converter":"org.apache.kafka.connect.json.JsonConverter",
       "key.converter.schemas.enable":false,
       "value.converter":"org.apache.kafka.connect.json.JsonConverter",
       "value.converter.schemas.enable":false,
       "schemas.enable":false
   }
}' &&
echo "\nWaiting 5 seconds for Kafka Connect to start...\n"
sleep 5 && # Wait for Kafka Connect to start

echo "Starting Flink jobs..."

# Run Flink jobs
/opt/flink/bin/flink run --detached /opt/flink/CustomerViewJob/target/customerview-1.0.jar &&
/opt/flink/bin/flink run --detached /opt/flink/RestaurantViewJob/target/restaurantview-1.0.jar &&
/opt/flink/bin/flink run --detached /opt/flink/DishViewJob/target/dishview-1.0.jar &&

echo "Flink jobs started."

# Keep container running
while true; do
  sleep 60  # Adjust the sleep duration based on your needs
done

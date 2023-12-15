#!/bin/bash

# Set environment variables
export KAFKA_URI=kafka:29092
export MONGODB_SERVER=mongodb://mongo:27017

# Array of Kafka topics to check
topics=(
  "postgres.public.certification"
  "postgres.public.customer"
  "postgres.public.customer_address"
  "postgres.public.dish"
  "postgres.public.dish_ingredient"
  "postgres.public.fidelity_card"
  "postgres.public.ingredient"
  "postgres.public.item"
  "postgres.public.order"
  "postgres.public.payment_method"
  "postgres.public.restaurant_address"
  "postgres.public.restaurant_info"
  "postgres.public.restaurant_review"
  "postgres.public.restaurant_service"
  "postgres.public.reviews_dish"
  "postgres.public.supplier"
)

# Function to check if Kafka topic exists
topic_exists() {
  local topic_name=$1
  local kafka_bootstrap_servers=$KAFKA_URI

  # Check if the topic exists
  /opt/flink/kafka_2.13-3.0.0/bin/kafka-topics.sh --bootstrap-server $kafka_bootstrap_servers --list | grep -Fxq $topic_name
}

is_system_initialized() {
    for topic in "${topics[@]}"; do
        if ! topic_exists $topic; then
            return 1
        fi
    done
    return 0
}

if ! is_system_initialized; then
    populate_kconnect() {
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
        }'
    }

    echo "Starting jobmanager..."
    /opt/flink/bin/jobmanager.sh start

    # Wait until the Kafka Connect service is available
    while ! curl -s 'http://kconnect:8083/connectors' > /dev/null; do
    echo "Waiting for Kafka Connect to be available..."
    sleep 5
    done

    sleep 5
    echo "Kafka Connect is now available. Sending Kafka Connectors to Kafka Connect..."

    populate_kconnect &&
    echo "\nWaiting for Kafka topics to be available...\n"


    # Check if all topics exist
    for topic in "${topics[@]}"; do
    while ! topic_exists $topic; do
        echo "Waiting for Kafka topic $topic to be available..."
        populate_kconnect
        sleep 5
    done
    echo "Kafka topic $topic is now available."
    done
fi

# Continue the program after all topics are available
echo "All Kafka topics are available. Continuing the program..."

# Run Flink jobs
/opt/flink/bin/flink run --detached /opt/flink/CustomerViewJob/target/CustomerViewJob-1.0.jar
/opt/flink/bin/flink run --detached /opt/flink/RestaurantViewJob/target/restaurantview-1.0.jar
/opt/flink/bin/flink run --detached /opt/flink/DishViewJob/target/dishview-1.0.jar

echo "Flink jobs started."

# Keep container running
while true; do
  sleep 60  # Adjust the sleep duration based on your needs
done

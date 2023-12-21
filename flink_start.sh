#!/bin/bash

########### SET VARIABLES ###########
export KAFKA_URI=kafka:29092
export MONGODB_SERVER=mongodb://mongo:27017

# Array of Kafka topics to check
topics=(
  "postgres.public.customer"
  "postgres.public.customer_address"
  "postgres.public.fidelity_card"
  "postgres.public.ingredient"
  "postgres.public.item"
  "postgres.public.order"
  "postgres.public.payment_method"
  "postgres.public.restaurant_address"
  "postgres.public.restaurant_info"
  "postgres.public.restaurant_review"
  "postgres.public.restaurant_service"
  "postgres.public.dish"
  "postgres.public.dish_ingredient"
  "postgres.public.dish_reviews"
  "postgres.public.supplier"
  "postgres.public.certification"
)



##### FUNCTIONS #####
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

# Function to check if Kafka topic exists
topic_exists() {
  local topic_name=$1
  local kafka_bootstrap_servers=$KAFKA_URI

  # Check if the topic exists
  /opt/flink/kafka_2.13-3.0.0/bin/kafka-topics.sh --bootstrap-server $kafka_bootstrap_servers --list | grep -Fxq $topic_name
}

# Function to check if all Kafka topics exist
is_system_initialized() {
    local kafka_bootstrap_servers=$KAFKA_URI
    local existing_topics
    existing_topics=$(/opt/flink/kafka_2.13-3.0.0/bin/kafka-topics.sh --bootstrap-server $kafka_bootstrap_servers --list)

    for topic in "${topics[@]}"; do
        if ! echo "$existing_topics" | grep -Fxq "$topic"; then
            echo "Topic $topic does not exist, system is not initialized, exiting..."
            return 1
        fi
    done
    return 0
}

# Function to run Flink job with retries
run_flink_job() {
  local job_jar=$1
  local max_retries=$2
  local sleep_duration=$3
  local retries=0

  echo "Starting Flink job $job_jar..."

  while [ $retries -lt $max_retries ]; do
    /opt/flink/bin/flink run --detached $job_jar && return 0  # Return success if the job starts successfully

    echo "Failed to start Flink job. Retrying in $sleep_duration seconds..."
    sleep $sleep_duration
    ((retries++))
  done

  echo "Max retries reached. Unable to start Flink job."
  return 1  # Return failure if all retries fail
}

########### MAIN ###########
echo "Starting jobmanager..."
/opt/flink/bin/jobmanager.sh start

if ! is_system_initialized; then
    echo "System is not initialized. Initializing system..."


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
        sleep 1
    done
    echo "Kafka topic $topic is now available."
    done
fi

echo "System is initialized. Starting Flink jobs..."


# Example usage
run_flink_job "/opt/flink/CustomerViewJob/target/CustomerViewJob-1.0.jar" 10 1
run_flink_job "/opt/flink/RestaurantViewJob/target/restaurantview-1.0.jar" 10 1
run_flink_job "/opt/flink/DishViewJob/target/dishview-1.0.jar" 10 1


echo "Flink jobs started."

# Keep container running 
#Update: without it, the container crashes!
while true; do
  sleep 60  # Adjust the sleep duration based on your needs
done

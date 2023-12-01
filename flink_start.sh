#!/bin/bash

# Set environment variables
export KAFKA_URI=kafka:29092
export MONGODB_SERVER=mongodb://mongo:27017

echo "Starting jobmanager..."
/opt/flink/bin/jobmanager.sh start

echo "Starting Flink jobs..."

# Run Flink jobs
flink run --detached /opt/flink/CustomerViewJob/target/customerview-1.0.jar &&
flink run --detached /opt/flink/RestaurantViewJob/target/restaurantview-1.0.jar &&
flink run --detached /opt/flink/DishViewJob/target/dishview-1.0.jar &&

echo "Flink jobs started."

# Keep container running
while true; do
  sleep 60  # Adjust the sleep duration based on your needs
done

#!/bin/bash

KAFKA_CONNECT_URL='http://localhost:8083/connectors'

# Set your database and Kafka configurations
DATABASE_HOST="postgres"
DATABASE_PORT="5432"
DATABASE_USER="postgres"
DATABASE_PASSWORD="postgres"
DATABASE_NAME="flinkfood"
KAFKA_SERVER_NAME="postgres"

# Specify the list of tables to include
TABLES=("public.restaurant_info" "public.restaurant_branch" "public.customer" "public.dishes" "public.dish_ingredients" "public.ingredients" "public.review_dish" "public.orders" "public.supplier" "public.items")

# Loop through the tables and create connectors
for TABLE in "${TABLES[@]}"; do
    CONNECTOR_NAME="${TABLE//./-}-connector"

    # Create connector configuration
    CONNECTOR_CONFIG='{
        "name": "'$CONNECTOR_NAME'",
        "config": {
            "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
            "database.hostname": "'$DATABASE_HOST'",
            "database.port": "'$DATABASE_PORT'",
            "database.user": "'$DATABASE_USER'",
            "database.password": "'$DATABASE_PASSWORD'",
            "database.dbname": "'$DATABASE_NAME'",
            "database.server.name": "'$KAFKA_SERVER_NAME'",
            "table.include.list": "'$TABLE'"
        }
    }'

    # Post the connector configuration to Kafka Connect
    curl -X POST "$KAFKA_CONNECT_URL" -H 'Content-Type: application/json' -d "$CONNECTOR_CONFIG"
done

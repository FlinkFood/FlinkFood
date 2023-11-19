#!/bin/bash
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-users-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.client",
        "slot.name": "users_replication_slot"
    }
}'

curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-restaurants-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.restaurant",
        "slot.name": "restaurants_replication_slot"
    }
}'

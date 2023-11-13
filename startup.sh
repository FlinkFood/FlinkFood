#!/bin/bash

curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-users-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgresuser",
        "database.password": "postgrespw",
        "database.dbname": "flink-food",
        "database.server.name": "postgres",
        "table.include.list": "public.users"
    }
}'

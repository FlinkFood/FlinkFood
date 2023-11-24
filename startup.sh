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
        "table.include.list": "public.customer"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-restaurant_info-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.restaurant_info",
        "slot.name": "restaurant_info_replication_slot"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-restaurant_services-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.restaurant_services",
        "slot.name": "restaurant_services_replication_slot"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-restaurant_address-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.restaurant_address",
        "slot.name": "restaurants_address_replication_slot"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-restaurant_reviews-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.restaurant_reviews",
        "slot.name": "restaurants_reviews_replication_slot"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-dishes-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.dishes",
        "slot.name": "dishes_replication_slot"
    }
}'
curl -X POST 'http://localhost:8083/connectors' -H 'Content-Type: application/json' -d '{
    "name": "postgres-reviews_dish-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "postgres",
        "database.dbname": "flinkfood",
        "database.server.name": "postgres",
        "table.include.list": "public.reviews_dish",
        "slot.name": "reviews_dish_replication_slot"
    }
}'

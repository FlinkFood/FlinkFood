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

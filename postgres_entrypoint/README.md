# Documentation

On google drive we uploaded the [db_schema](https://drive.google.com/drive/folders/1nASJjyaI5SR9WCRJccmKuF-hFU37CH_S)

The tables should be already in place and populated.
When creating a fresh new postgres container the db is initialised from the file [init.sql](init.sql).

Adding more data is possible by modifying the files in the [import](import) folder and running the script [import.sh](import.sh).
```bash
docker exec postgres psql -U postgres -f /docker-entrypoint-initdb.d/init.sql
```

To check the updated tables in the flinkfoodBD run:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "\dt"
```

It's possible to see also the new data inserted in the tables with:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "SELECT * FROM restaurant_info"
```

Personally I prefer using a tool to connect to the DB flinkfood@0.0.0.0:5432 with password and user *postgres*
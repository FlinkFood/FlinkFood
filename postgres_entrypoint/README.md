# Documentation

On google drive we uploaded the [db_scheama](https://drive.google.com/drive/folders/1nASJjyaI5SR9WCRJccmKuF-hFU37CH_S)

The tables should be already in place and populated.
If this is not the case you should delete the postgres docker and recreate it.


To check the updated tables in the flinkfoodBD run:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "\dt"
```

It's possible to see also the new data inserted in the tables with:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "SELECT * FROM restaurants"
```

Personally I prefer using a tool to connect to the DB flinkfood@0.0.0.0:5432 with password and user *postgres*
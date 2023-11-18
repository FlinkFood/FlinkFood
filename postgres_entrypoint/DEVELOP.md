# Useful info while working with the POSTGRE DB
to enter the docker and execute the sql file run: (to be automated!)
```bash
docker exec -it postgres psql -U postgres -d flinkfood -f /docker-entrypoint-initdb.d/restaurants.sql
```

to show the updated tables in the flinkfoodBD run:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "\dt"
```

It's possible to see also the new data inserted in the tables with:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "SELECT * FROM restaurants"
```

note: executing the following command:
```bash
 docker exec postgres psql -U postgres -c "\l"
```
It's possible to see that many DBs are present. (not useful for now)
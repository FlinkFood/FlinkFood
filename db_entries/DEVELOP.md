# Useful info while working with the POSTGRE DB
to show the DB schema run:
```bash
docker exec postgres psql -U postgres -d flinkfood -c "SELECT schema_name FROM information_schema.schemata"
```
nothing will be there because it's not automated the running but ok for now.
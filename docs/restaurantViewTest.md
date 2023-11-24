### Execute the [`startup.sh`](./startup.sh) script to create a Kafka topic connected to the users table in Postgres (this step will be automated in the future). Go to `http://localhost:9000` and check if the new topics `postgres.public` was successfully added.


### Connect to your Postgres database and add a new restaurant to the table:

```bash
docker exec -it postgres psql -U postgres -d flinkfood -c \
"INSERT INTO public.restaurant_info (id,name,phone,email,cuisine_type,price_range,vat_code) VALUES (10, 'Test', 123456789, 'test@test.te', 'seafood', 'low', 123456789);"
```  
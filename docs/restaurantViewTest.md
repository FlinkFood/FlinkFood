# RestaurantView Test
1. First make sure all the containers are correctly reset:
```bash
 docker compose down -v
```

2. Now, run all the docker containers:
```bash
docker compose up -d
```
This will run the containers in the background.

3. Wait for some seconds to make sure the containers are properly started. Then we want to create the relevant kafka topics and kafka connectors to debezium. This is done by running the following command:
```bash
../startup.sh
```

**NOTE:** Usually the creation of these topics are quite slow (a couple of minutes), since kafka and debezium needs to initialize everything and we have a bunch of topics.
You can check the creation of topics by going to [kafdrop dashboard](http://localhost:9000`) and check if these topics are created:
- postgres.public.customer
- postgres.public.dishes
- postgres.public.restaurant_address
- postgres.public.restaurant_info
- postgres.public.restaurant_reviews
- postgres.public.restaurant_services
- postgres.public.reviews_dish

4. Start the flink job by running the `RestaurantView.java` in your prefered IDE.

5. Try INSERTING/UPDATING or DELETING some entries:
```bash
docker exec -it postgres psql -U postgres -d flinkfood -c \
"INSERT INTO public.restaurant_info (id,name,phone,email,cuisine_type,price_range,vat_code) VALUES (10, 'Test', 123456789, 'test@test.te', 'seafood', 'low', 123456789);"
```

6. After some time (seconds) you should be able to view the mongoDB database and see a document collection under `flinkfood/restaurants_view`. 



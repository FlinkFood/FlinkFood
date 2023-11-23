# How to run Flinkfood

The next few steps explain how to run the Job that makes the first letter of a user name uppercase:

### 1. Start Containers with Docker Compose

Run the following command to start all the containers:

```bash
docker-compose up -d
```

### 2. Execute the Startup Script
Execute the [`startup.sh`](./startup.sh) script to create a Kafka topic connected to the users table in Postgres (this step will be automated in the future). Go to `http://localhost:9000` and check if the new topic `postgres.public.client` was successfully added.

### 3. Set Environment Variables
Set the `MONGODB_URI` and `KAFKA_URI` environment variables for your application. For example:
```bash
export MONGODB_URI=mongodb://localhost:27017
export KAFKA_URI=localhost:9092
```

### 4. Compile and Run the Java Job
Compile and run the [`FirstLetterUppercase.java`](./flinkfood-demo/src/main/java/org/flinkfood/flinkjobs/FirstLetterUppercase.java) job. Make sure you are using **JDK11**. It is easier to do this via an IDE, but it can also be done by running `mvn clean package` to compile the code and run it with the `java` command.

### 5. Connect to Postgres and Add a New User
Connect to your Postgres database and add a new user to the table:
```bash
docker exec -it postgres psql -U postgres -d flinkfood -c "INSERT INTO public.customer (id,username,first_name,last_name,birthdate,email,fiscal_code) VALUES (6, 'test', 'test', 'test', '2021-01-01', 'test', 'test');"
```
A new entry will be added to the [MongoDB](mongodb://localhost:27017) at flinkfood/collections/users_sink
with username = "TEST".
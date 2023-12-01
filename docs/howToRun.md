# How to run Flinkfood

This file is intended to give you an introduction on how to run the Flinkfood application. There is an easy way. And there is an hard way, which contains more details on how things are setup.

## The easy way
You can start the Flinkfood application by running this simple command. It will compile your program and submit your flinkjobs to the Flink jobmanager. You can monitor your jobs after running this command at the [Flinkfood dashboard](http://localhost:8081/#/overview).

Command to run:
```bash
docker-compose up -d --build
```

**NOTE:** Since this command does alot of things. You might have to wait some minutes before things are fully up and running. However, most of the computation is cached for future runs of this commands, making it much faster.

## The hard way
### 1. Start Containers with Docker Compose

Run the following command to start all the containers:

```bash
docker-compose up -d
```

### 2. Execute the Startup Script
Execute the [`startup.sh`](../startup.sh) script to create a Kafka topic connected to the users table in Postgres (this step will be automated in the future). Go to `http://localhost:9000` and check if the new topic `postgres.public.client` was successfully added.

### 3. Set Environment Variables
Set the `MONGODB_URI` and `KAFKA_URI` environment variables for your application. For example:
```bash
export MONGODB_URI=mongodb://localhost:27017
export KAFKA_URI=localhost:9092
```

### 4. Compile and Run the Java Job
Compile and run the [`FirstLetterUppercase.java`](../flinkfood-demo/src/main/java/org/flinkfood/flinkjobs/FirstLetterUppercase.java) job. Make sure you are using **JDK11**. It is easier to do this via an IDE, but it can also be done by running `mvn clean package` to compile the code and run it with the `java` command.

### 5. Connect to Postgres and Add a New User
Connect to your Postgres database and add a new user to the table:
```bash
docker exec -it postgres psql -U postgres -d flinkfood -c "INSERT INTO public.customer (id,username,first_name,last_name,birthdate,email,fiscal_code) VALUES (6, 'test', 'test', 'test', '2021-01-01', 'test', 'test');"
```
A new entry will be added to the [MongoDB](mongodb://localhost:27017) at flinkfood/collections/users_sink
with username = "Test". (duplicated for some reasons)

# How to run Flinkfood

This file is intended to give you an introduction on how to run the Flinkfood application. There is an easy way. And there is an hard way, which contains more details on how things are setup.

## Prerequisites
A requisite to be able to run the containers is to have the Docker Engine and CLI on your machine. One thing you can do is to install [Docker Desktop](https://docs.docker.com/desktop/) which includes everything we will need plus a nice GUI that makes our lives easier.

I highly recommend to follow the Instalation Guide from the [official Docker documentation](https://docs.docker.com/), accordingly with your OS:
- [Linux]((https://docs.docker.com/desktop/install/linux-install/))
- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [MacOS](https://docs.docker.com/desktop/install/mac-install/)

After installing this, I suggest to check that everything went well by checking the version of the following commands. In a terminal, run:
```bash
$ docker -v
```
And you can expect an output in the format of:
```
Docker version 24.0.6, build ed223bc
```

In the same way, let us check the installation of docker compose. In a terminal, run:
```bash
$ docker compose version
```
And you can expect an output in the format of 
```
Docker Compose version v2.22.0-desktop.2
```

### Quick Note

Before running docker compose, make sure that the Docker Deamon is running on your computer (a quick observation is that if you open Docker Desktop it runs it automatically).

Docker uses a daemon-based architecture where the CLI connects to a long-lived process running separately on your machine or a remote host. CLI commands won't work and your containers will usually go offline if the daemon stops.

Here's how to check whether Docker's daemon is up so you can diagnose issues with containers and the

```bash
$ docker
```

command. When the daemon's not running, you'll see a 
`"can't connect to Docker daemon"` message.

To start the deamon, follow the steps on the [Docker Docs page](https://docs.docker.com/config/daemon/start/).

## The easy way
You can start the Flinkfood application by running this simple command. It will compile your program and submit your flinkjobs to the Flink jobmanager. You can monitor your jobs after running this command at the [Flinkfood dashboard](http://localhost:8081/#/overview).

Command to run:
```bash
docker-compose up -d --build
```

**NOTE:** Since this command does alot of things. You might have to wait some minutes before things are fully up and running. However, most of the computation is cached for future runs of this commands, making it much faster.

### What is this flink_start.sh file?
This file is used by the docker container to start some flink process and populate the kafka topics. We do not have to run this file manually, as it is only used by the docker container.

--- 

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

### 4. Compile and Run the Java Jobs
#### 4.1 Using Maven
To compile and run the command
```bash
cd ../flinkfood-demo; mvn clean package
```

This is going to create a `.jar` file for each Flink job. The .jar files are going to be placed in the following locations:

| Flink Job | Location of .jar file |
| ----------| --------------------- |
| CustomerViewJob |`./flinkfood-demo/CustomerViewJob/target/customerview-1.0.jar` |
| RestaurantViewJob | `./flinkfood-demo/RestaurantViewJob/target/restaurantview-1.0.jar` |
| DishViewJob |`./flinkfood-demo/DishViewJob/target/dishview-1.0.jar` |
 
These jobs can now be submitted to the [Flink dashboard](localhost:8081).

#### 4.2 Using an IDE
Sometimes it can be a benefit to quickly be able to test if your Flinkjob is doing what you would expect in a quick way. 

##### Intellij
Before running, make sure you have gone to run configuration. In this menu click "Modify Options" and select the entry `Add depedency with "provided" scope to classpath`

Now, if you have followed the previous steps of the "Hard way" you can press the Run button and your service will start

### 5. Connect to Postgres and Add a New User
Connect to your Postgres database and add a new user to the table:
```bash
docker exec -it postgres psql -U postgres -d flinkfood -c "INSERT INTO public.customer (id,username,first_name,last_name,birthdate,email,fiscal_code) VALUES (6, 'test', 'test', 'test', '2021-01-01', 'test', 'test');"
```
A new entry will be added to the [MongoDB](mongodb://localhost:27017) at flinkfood/collections/users_sink
with username = "Test". (duplicated for some reasons)

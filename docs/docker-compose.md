# Docker Compose: how to run stuff?

## Overview

Hello my fellow devs. This file is meant to document and help with some key points to run de docker containers via the docker compose. 

## Pre-requisites

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

## Running docker compose

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

### Running containers with docker compose

To run the containers on the compose file. Open a terminal and run the following command:
```bash
$ docker compose up -d
```

### Checking running containers

It can be done both via the GUI of Docker Desktop or via CLI, by running on a terminal:
```bash
$ docker ps
```
It will list all running containers, their IDs and related information.

### Entering a container

To log in a container and access its shell, you can use the following command:
```bash
$ docker exec -it [CONTAINER NAME OR ID] sh
```

### Postgresql container

To log in a container and performs queries, you can use the following command:
```bash
$ docker exec -it [CONTAINER NAME OR ID] sh
```
Once inside the container, you run:
```bash
$ psql -U [USERNAME] -d [DB_NAME]
```
As an example, we might have:
```bash
$ psql -U postgresuser -d shipment_db
```
After this, you can perform queries in your database.

To exit, both the interface of postgresql and the container, you can use the command `exit`.

### Kafka container

After entering the kafka container, to list topics, you can run the following command:
```bash
$ kafka-topics --list --zookeeper localhost:2181
```
And to see the content of a specific topic, you can use:
```bash
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic [TOPIC_NAME] --from-beginning
```

It might be a better way to visualize these streams, but we didn't go into that yet. So work to be done in the future.

### Debezium container

Here let us see how to register the Postgres connector in Debezium, or in other words, to tell Debezium to stream changes from the databse, we will use `shipment_db` as an example here. We do this by registering a connector.

Log in to the Debezium container and execute a command in this format in a terminal (for example purposes I will use the `shipment_db`):

```bash
curl -H 'Content-Type: application/json' debezium:8083/connectors --data '
{
  "name": "shipments-connector",  
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector", 
    "plugin.name": "pgoutput",
    "database.hostname": "postgres", 
    "database.port": "5432", 
    "database.user": "postgresuser", 
    "database.password": "postgrespw", 
    "database.dbname" : "shipment_db", 
    "database.server.name": "postgres", 
    "table.include.list": "public.shipments" 
  }
}'
```
## Final observations

This is a live document.

The source of the shipment database example can be found [here](https://medium.com/event-driven-utopia/configuring-debezium-to-capture-postgresql-changes-with-docker-compose-224742ca5372).
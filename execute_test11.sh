#!/bin/bash

"$(docker compose up -d --build)"

while true
do
	#Test if all containers are running or not
	if [[ "$(docker inspect grafana --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect prometheus --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect kafdrop --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect mongo --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect taskmanager --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect jobmanager --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect kconnect --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect kafka --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect postgres --format '{{.State.Status}}')" != "running" ||
	     "$(docker inspect zookeeper --format '{{.State.Status}}')" != "running" ]];
	then
    		echo "One or more container failed. Reinitializing: "
    		"$(docker compose down -v)"
    		"$(docker compose up -d --build)"
	else
    		echo "The containers are running!"
		sleep 5
	fi
done


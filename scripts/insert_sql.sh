#Considering the postgres container already running
#.../scripts
sql=$(cat flink-food.sql)

#Accessing the docker
docker exec -it flinkfood-postgres-1 sh

#Entering PSQL area
psql -U postgresuser -d flink-food -c "$sql" 
 
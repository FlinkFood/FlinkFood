# Acceptance test

## Prerequisites

### 1. Making sure the system runs
Before running the acceptance test, please make sure you have ran the docker containers using the command:

```bash
docker compose up -d --build
```

Make sure that everything is working as expected by running some test queries in the postqres database which can be accessed:

```bash
docker exec -it postgres psql -U postgres -d flinkfood 
```

### 2. Setting up the Grafana Dashboard
The Grafana dashboard works in such a way that you would have to configure it and it is then stored in the docker container. There is probably better ways to persist this, but we did not have enough time. So please follow theseinstructions to make the dashboard before the acceptance test/demo:


#### 1. Start Docker Services:

Start your Docker services using Docker Compose.

```bash
    docker-compose up -d
```

#### 2. Access Grafana Dashboard:
Open your web browser and navigate to http://localhost:3000. Log in with the credentials:
        Username: admin
        Password: flink

#### 3. Configure Prometheus Data Source:
1. Click on "Add your first data source"
4. Choose "Prometheus" from the list.
5. In the Connection section, set the URL to http://prometheus:9090.
6. Scroll down to the bottom and click "Save & Test" to verify the connection.
7. It should say "Successfully queried the Prometheus API".

#### 4. Import Flink Dashboard:
1. Click on the menu icon in the top left under the Grafana logo.
2. Click "Dashboards".
3. Select "+ Create Dashboard"
3. Select "Import dashboard"
3. Use the Grafana Dashboard ID: `14911` and click "Load". Here you can experiment with [**Different Grafana Dashboards for Apache Flink**](https://grafana.com/grafana/dashboards/?search=Flink)
4. Choose the Prometheus data source.
5. Click "Import."
6. Now you should see the dashboard which consists of a lot of dropdown menus.

#### 5. Explore Flink Metrics:
Explore and visualize Flink metrics on the Grafana dashboard. Customize and adjust widgets based on your specific metrics and requirements.

## The acceptance test plan
The following steps are made to ensure that we manage to test everything for the acceptance test:

### Test 2: Changing  data
Run the following command and check if the email for "Ristorante Di Mario" is updated:

```sql
UPDATE restaurant_info SET email='mario@gmail.com' WHERE id = 1;
```

### Test 3: Data validation
Run the following commands and check if "Ristorante Di Mario" has the information present in the tables:

General info:
```sql
SELECT * FROM restaurant_info WHERE id=1;
```

Services:
```sql
 SELECT * FROM restaurant_service WHERE restaurant_id = 1;
```

Branches:
```sql
SELECT * FROM restaurant_address WHERE restaurant_id = 1;
```

Reviews:
```sql
SELECT * FROM restaurant_review WHERE restaurant_id = 1;
```

Served dishes:
```sql
SELECT * FROM dish WHERE restaurant_id = 1;
```

### Test 5: Logging and auditioning
Run the following command:

```sql
UPDATE restaurant_info SET email='mario2@gmail.com' WHERE id = 1;
```

As of writing this, there is no logs stored, so the post conditions is not fulfilled.

### Test 6: API effectiveness
There is no API interface implemented as the team deemed it sufficient along with the customer to not implement this.

### Test 7: Single view filtering
There is no API interface implemented as the team deemed it sufficient along with the customer to not implement this.

### Test 8: Reliability
1. Leave the job running for 1 hour.
2. Go to the [Grafana Dashboard](http://localhost:3000)
3. Go to the list of Dashboards in the top left menu
4. Click the dashboard named "Apache Flink (2021) Dashboard for Job / Task Manager"
5. Open the dropdown "Job Manager (Slots & Jobs)"

The test passes if the uptimes are more than 1 hour for each job.

### Test 9: Data recovery

With the system up and running, we will purposely introduce an error.

Deleting onf of the tables:
```sql
DROP TABLE restaurant_info
```

The system should fail. Verify, then, if the mongoDB still contains at least 95% of the original data

### Test 10: Ease of use
First reset the entire system:

```sh
docker compose down -v
```

```sh
docker rmi $(docker images -q)
```

Then run this command to start up the system.

```sh 
docker compose up -d --build
```

Measure the time from this command is run till the first single view appears in MongoDB. If this is less than 5 hours, the test has successfully passed.

### Test 11: Robustness
Run the file:

```sh
./execute_test11.sh
```

This file should ensure the system will be restarted after any failure. Then introduce a failure, by, for example, stopping a container

Verify if the system will be restarted within 5 minutes.


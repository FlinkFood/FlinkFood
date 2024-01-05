# Metrics

This document is designed to help the user set up a Grafana Dashboard for the FlinkFood engine

## Why do I need a Grafana dashboard?
The Grafana dashboard can provide and vizualize important metrics from the FlinkFood engine. For instance, time spent on each job. This can be used for troubleshooting and optimization.

## Setup the Grafana dashboard
Running the Grafana dashboard and connecting it to Flink using Prometheus is already done by the docker compose system. However, there are some manual setup needed to get a dashboard. This gives an overline of how this is done.

### 1. Start Docker Services:

Start your Docker services using Docker Compose.

```bash
    docker-compose up -d
```

### 2. Access Grafana Dashboard:
Open your web browser and navigate to http://localhost:3000. Log in with the credentials:
        Username: admin
        Password: flink

### 3. Configure Prometheus Data Source:
1. Click on the gear icon (⚙️) on the left sidebar.
2. Select "Data Sources."
3. Click "Add your first data source."
4. Choose "Prometheus" from the list.
5. In the HTTP section, set the URL to http://prometheus:9090.
6. Click "Save & Test" to verify the connection.

### 4. Import Flink Dashboard:
1. Click on the "+" icon on the left sidebar.
2. Select "Import."
3. Use the Grafana Dashboard ID: `14911`. Here you can experiment with [**Different Grafana Dashboards for Apache Flink**](https://grafana.com/grafana/dashboards/?search=Flink)
4. Choose the Prometheus data source.
5. Click "Import."

### 5. Explore Flink Metrics:
Explore and visualize Flink metrics on the Grafana dashboard. Customize and adjust widgets based on your specific metrics and requirements.

### Conclusion:

You have successfully set up Grafana to visualize Flink metrics using Prometheus as a data source. Explore the power of monitoring and analyzing your Flink application with Grafana dashboards.

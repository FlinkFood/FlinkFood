# FlinkFood

## Overview

FlinkFood is a project that addresses the challenge of providing users with informed decisions about restaurant choices, customized recommendations, and real-time delivery management. The project aims to implement a Data Fabric solution using Apache Flink®.

## Motivation

Every day, users place orders on FlinkFood, each with unique preferences and requirements. Restaurants continually update their menus, creating a need for an efficient data aggregation system. FlinkFood is designed to meet this need by implementing a Data Fabric solution with Apache Flink.

## Code Style

TBD

## Features

TBD

## Tests

Comprehensive tests are yet to be defined for the project. Test examples and guidelines will be added in future updates.

## How to Use

To use FlinkFood, follow these steps:

### How to run

See the [documentation for how to run FlinkFood](docs/howToRun.md) for more details on these steps.

### Basic docker commands

Using the following commands will start docker containers for Kafka and Apache Flink. Flink is running in session mode. This means that we have a long-running Flink Cluster which we can submit jobs to. This is preferably done in the web UI (see below). For more information on this configuration of Apache Flink, see: [Apache Flink Documentation](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/resource-providers/standalone/docker/#session-mode-1)

1. Launch a cluster in the foreground (use -d for background)

```sh
docker-compose up
```

2. Access Web UI
   When the cluster is running, you can visit the web UI at http://localhost:8081.

3. Kill the cluster

```sh
docker-compose down
```

### Interacting with the different containers

#### PostgreSQL container

Run the following command to enter the PostgreSQL container:

```sh
docker exec -it flinkfood-postgres-1 sh
```

To enter the postgreSQL database by running this command:

```sh
psql -U postgresuser -d shipment_db
```

Now you can run SQL commands :)

### Optional configuration

- Scale the cluster up or down to N TaskManagers

```sh
docker-compose scale taskmanager=<N>
```

- Access the JobManager container

```sh
docker exec -it $(docker ps --filter name=jobmanager --format={{.ID}}) /bin/sh
```

## Contribute

Contributions to FlinkFood are welcome! Follow these steps to contribute:

1. Create a new branch for your feature from the develop branch (`git checkout -b JRA-123-<branch-name>`)
2. Make changes and commit (`git commit -m 'JRA-123 <commit message>'`)
3. Push to the branch (`git push origin JRA-123-<branch-name>`)
4. Create a pull request into the develop branch

Please follow the [Git flow workflow](https://www.gitkraken.com/learn/git/git-flow) and adhere to the established code style.

To make sure that the issues, commits and PRs are linked to the Jira issues. Make sure to follow these rules:

### Issue naming

Please be sure to name your issue (if the ID of the Jira task is FLIN-123):
`[FLIN-123] Name of issue`
This will automatically link the issue with your issue on Jira

### Branch naming

`git checkout -b FLIN-123-<branch-name>`

### Commit naming

`git commit -m "FLIN-123 <commit message>"`

### Pull requests (PRs)

Do at least one of the following:

- Include a commit in the pull request that has the issue key in the commit message. Note, the commit cannot be a merge commit.
- Include the issue key in the pull request title.
- Ensure that the source branch name also includes the issue key in the branch name.

Please name your PR in the following format:
`[FLIN-123] Name of pull request`

### Reviews

Include the issue key at the beginning of the review title when you create the review to link the review to your Jira issue.

For example, name your review "JRA-123 <review summary>" and start the review.

## Credits

We would like to give credit to the following:

## License

FlinkFood is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

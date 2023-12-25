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

2. Apache Flink Web Dashboard
   When the cluster is running, you can visit the [Apache Flink Web Dashboard](http://localhost:8081) for manual management.

3. Kill the cluster

```sh
docker-compose down
```

### Interacting with the different containers

#### PostgreSQL container

Run the following command to enter the PostgreSQL container:

```sh
docker exec -it flinkfood-postgres-1 bash
```

To enter the postgreSQL database by running this command:

```sh
psql -U postgres -d flinkfood
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

1. Create a new branch for your feature from the develop branch (`git checkout -b "(feature | hotfix | release | docs) - FLIN - #of_task - really_brief_description"`) no spaces.
2. Make changes, re-run the docker containers to test and commit (`git commit -m 'simple_commit_message'`, eg: `git commit -m 'update_README.md'`). In case you have done the work along-side someone (Parallel computing), you can commit with them as co-authors (how to do it: [here](https://docs.github.com/en/pull-requests/committing-changes-to-your-project/creating-and-editing-commits/creating-a-commit-with-multiple-authors))
3. Push to the branch (`git push origin 'name_of_created_branch'`)
4. Create a pull request into the develop branch (details below). Make sure to add the reviewers of the work as the co-collaborators assigned on the Ja board.
5. Then the pull request should be squashed merged to include all the commits in the merge commit.

Please follow the [Git flow workflow](https://www.gitkraken.com/learn/git/git-flow) and adhere to the established code style.

### Pull requests (PRs)

- Before opening the PR make sure you are up to date ith `develop` branch
- After the PR is approved squash merge your commits on develop

Do at least one of the following:

- Include a commit in the pull request that has the issue key in the commit message. Note, the commit cannot be a merge commit.
- Include the issue key in the pull request title. You can do it by just adding a "#" and the number of the issue. 
- Ensure that the source branch name also includes the issue key in the branch name.

Please name your PR in the following format:
`[FLIN-#of_task] Description of FLIN-#of_task as presented on Jira`

### Reviews

Name your review "JRA-123 <review summary>" and start the review. Follow the Git Hub steps to do it. Communicate thought the GitHub interface commenting, approving or requesting changes.

## Credits

We would like to give credit to the following:

## License

FlinkFood is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

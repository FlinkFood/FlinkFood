# FlinkFood

## Overview

FlinkFood is a project that addresses the challenge of providing users with informed decisions about restaurant choices, customized recommendations, and real-time delivery management. The project aims to implement a Data Fabric solution using Apache Flink®.

## Motivation

Every day, users place orders on FlinkFood, each with unique preferences and requirements. Restaurants continually update their menus, creating a need for an efficient data aggregation system. FlinkFood is designed to meet this need by implementing a Data Fabric solution with Apache Flink.

## Code Style
The decided on coding style in this project is the Google Java Style Guide.

## Features
The FlinkFood application takes inputs from 18 different database tables. Uses CDC through debezium to capture changes in these tables and then creates a single view for the customer, restaurant and dishes. This single view is then stored in a MongoDB collection. The main point of this project is to test out the Apache Flink engine for the MIA Platform team and to assess risks and advantages with using the Apache Flink engine for data stream processing


## How to Use

To use FlinkFood, follow these steps:

### How to run

See the [documentation for how to run FlinkFood](docs/howToRun.md) for more details on these steps.

## Contribute

Contributions to FlinkFood are welcome! Follow these steps to contribute:

1. Create a new branch for your feature from the develop branch (`git checkout -b "(feature | hotfix | release | docs) - FLIN - #of_task - really_brief_description"`) no spaces.
2. Make changes, re-run the docker containers to test and commit (`git commit -m 'simple_commit_message'`, eg: `git commit -m 'update_README.md'`). In case you have done the work along-side someone (Parallel computing), you can commit with them as co-authors (how to do it: [here](https://docs.github.com/en/pull-requests/committing-changes-to-your-project/creating-and-editing-commits/creating-a-commit-with-multiple-authors))
3. Push to the branch (`git push origin 'name_of_created_branch'`)
4. Create a pull request into the develop branch (details below). Make sure to add the reviewers of the work as the co-collaborators assigned on the Ja board.
5. Then the pull request should be squashed merged to include all the commits in the merge commit.

Please follow the [Git flow workflow](https://www.gitkraken.com/learn/git/git-flow) and adhere to the established code style.

### Pull requests (PRs)

- Before opening the PR make sure you are up to date with `develop` branch
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

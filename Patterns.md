# Document for pattern exploration
## TODO:
- [ ] Create a huge set of data (I'd go with the restaurant views if it's okay for you)
- [ ] Find a tool to evaluate Flink performance -> cpu and memory usage

If we stick with the TableStream:
- [ ] Create a json/yaml whatever configuration file for the flink tables

## TableEnviroment Pattern
I've implemented a configurable sql command in the [config directory](./.config)
It's used to create a table enviroment for the data exploration. [here @ line 52](./flinkfood-demo/src/main/java/org/flinkfood/flinkjobs/RestaurantView.java#here)
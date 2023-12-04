# Document of pattern exploration

## TableEnviroment Pattern
I've implemented a configurable sql command in the [config directory](../../.config)
It's used to create a table enviroment for the data exploration. [here @ line 52](../../flinkfood-demo/src/main/java/org/flinkfood/flinkjobs/RestaurantTableView.java#here)

## DataSteam Pattern
1. Approach with datastreams connected and keyed with the right
  ids are flatmapped into a RestaurantView but they are not joined:
  the restaurantViews are created with distinct objects from the streams (not joined)
```Java
restaurantInfoDataStream
.map(restaurantInfo -> new RestaurantView().with(restaurantInfo))
.connect(restaurantAddressDataStream)
.keyBy(RestaurantView::getRestaurantId, RestaurantAddress::getRestaurantId)
.flatMap(new CoFlatMapFunctionImpl_())
.sinkTo(sink);
```

2. Approach connecting info and address dataStreams and joining them into
  the restaurantViews are created with joined objects from the streams
  the window is applied and I don't know whats the point
  the join function it's the best... but it's not working :)
  
``` Java
restaurantInfoDataStream
.join(restaurantAddressDataStream)
.where(RestaurantInfo::getId)
.equalTo(RestaurantAddress::getRestaurantId)
.window(TumblingEventTimeWindows.of(Time.seconds(5)))
.apply((JoinFunction<RestaurantInfo, RestaurantAddress, RestaurantView>)
(restaurantInfo, restaurantAddress) -> new RestaurantView().with(restaurantInfo).with(restaurantAddress))
.sinkTo(sink)
```

3. Approach with datastreams grouped based to the id,
  windowed?!?
  the restaurant view **should** be created with joined objects from the streams
  and collected and sinked... No idea where is actually going
 ``` Java
   restaurantInfoDataStream
   .coGroup(restaurantAddressDataStream)
   .where(RestaurantInfo::getId)
   .equalTo(RestaurantAddress::getRestaurantId)
   .window(TumblingEventTimeWindows.of(Time.hours(100)))
   .apply(new CoGroupFunctionImpl_())
   .sinkTo(sink);
```

package org.flinkfood.schemas.restaurant;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.mongodb.sink.config.MongoWriteOptions;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.mongodb.shaded.com.mongodb.client.model.UpdateOneModel;
import org.apache.flink.mongodb.shaded.org.bson.BsonElement;
import org.apache.flink.mongodb.shaded.org.bson.conversions.Bson;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.bson.*;
import org.flinkfood.schemas.dish.Dish;
import org.flinkfood.schemas.order.Order;
import org.flinkfood._helper.InsertBsonField;

import java.util.Collections;
import java.util.List;

public class RestaurantView {
    private RestaurantInfo restaurantInfo;
    private RestaurantService restaurantService;
    private RestaurantAddress restaurantAddress;
    private List<RestaurantReview> restaurantReviews;
    private List<Order> orders;
    private List<Dish> dishes;
    private List<ReviewDish> reviewDish;

    public RestaurantView() {
    }
    public RestaurantView with(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
        return this;
    }
    public RestaurantView with(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
        return this;
    }
    public RestaurantView with(RestaurantReview restaurantReview) {
        this.restaurantReviews.add(restaurantReview);
        return this;
    }
    public RestaurantView with(Dish dish) {
        this.dishes.add(dish);
        return this;
    }
    public RestaurantView with(ReviewDish reviewDish) {
        this.reviewDish.add(reviewDish);
        return this;
    }
    public RestaurantView with(Order order) {
        this.orders.add(order);
        return this;
    }
    public RestaurantView with(RestaurantAddress restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
        return this;
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public RestaurantService getRestaurantService() {
        return restaurantService;
    }

    public RestaurantAddress getRestaurantAddress() {
        return restaurantAddress;
    }

    public List<RestaurantReview> getRestaurantReviews() {
        return restaurantReviews;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public List<ReviewDish> getReviewDish() {
        return reviewDish;
    }

    public List<Order> getOrders() {
        return orders;
    }
    public int getRestaurantId() {
        return restaurantInfo.getId();
    }

    /*
     * Document composition, null safe
     */
    public BsonDocument toBson() {
        var doc = new BsonDocument();
        doc.append("restaurant_info", restaurantInfo == null ? new BsonDocument() : restaurantInfo.toBson());
        doc.append("restaurant_address", restaurantAddress == null ? new BsonDocument() : restaurantAddress.toBson());
        // FIXME: complete
//        doc.append("restaurant_service", restaurantService == null ? new BsonDocument() : restaurantService.toBson());
//        doc.append("restaurant_reviews", restaurantReviews == null ? new BsonArray() : new BsonArray(restaurantReviews.stream().map(RestaurantReview::toBson).toList()));
//        doc.append("dishes", dishes == null ? new BsonArray() : new BsonArray(dishes.stream().map(Dish::toBson).toList()));
//        doc.append("review_dish", reviewDish == null ? new BsonArray() : new BsonArray(reviewDish.stream().map(ReviewDish::toBson).toList()));
//        doc.append("orders", orders == null ? new BsonArray() : new BsonArray(orders.stream().map(Order::toBson).toList()));
        return doc;
    }

    public static class Serializer implements MongoSerializationSchema<RestaurantView>, InsertBsonField {

        @Override
        public void open(SerializationSchema.InitializationContext initializationContext, MongoSinkContext sinkContext, MongoWriteOptions sinkConfiguration) throws Exception {
            initializationContext.getMetricGroup().addGroup("MongoDB");
            System.out.print("test RESTAURANT VIEW");
        }

        @Override
        public WriteModel<BsonDocument> serialize(RestaurantView element, MongoSinkContext sinkContext) {
            return new InsertOneModel<>(element.toBson());
            // TODO: use this in case a Document of the view is already present
            // return new UpdateOneModel<RestaurantView>(...)
        }
    }

    public static class JoinRestaurantAddress implements CoMapFunction<RestaurantView, RestaurantAddress, RestaurantView> {

        @Override
        public RestaurantView map1(RestaurantView value) {
            return value;
        }

        @Override
        public RestaurantView map2(RestaurantAddress value) {
            return new RestaurantView().with(value);
        }
    }
}

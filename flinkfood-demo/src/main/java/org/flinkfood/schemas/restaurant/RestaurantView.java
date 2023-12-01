package org.flinkfood.schemas.restaurant;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.mongodb.sink.config.MongoWriteOptions;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.bson.*;
import org.flinkfood.schemas.dish.Dish;
import org.flinkfood.schemas.order.Order;
import org.flinkfood._helper.InsertBsonField;

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

    public static class Serializer implements MongoSerializationSchema<RestaurantView>, InsertBsonField {

        @Override
        public void open(SerializationSchema.InitializationContext initializationContext, MongoSinkContext sinkContext, MongoWriteOptions sinkConfiguration) throws Exception {
            initializationContext.getMetricGroup().addGroup("MongoDB");
            System.out.print("test RESTAURANT VIEW");
        }

        @Override
        public WriteModel<BsonDocument> serialize(RestaurantView element, MongoSinkContext sinkContext) {
            return new InsertOneModel<>(new BsonDocument().append("restaurant_id", new BsonInt32(element.getRestaurantId())));
        }
    }
}

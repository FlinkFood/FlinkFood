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

    public BsonDocument toBson() {
        return new BsonDocument()
                .append("restaurantId", new BsonInt32(restaurantInfo.getId()))
                .append("restaurantName", new BsonString(restaurantInfo.getName()))
                .append("restaurantAddress", new BsonDocument()
                        .append("street", new BsonString(restaurantAddress.getStreet()))
                        .append("city", new BsonString(restaurantAddress.getCity()))
                        .append("state", new BsonString(restaurantAddress.getCountry()))
                        //FIXME.append("zipCode", new BsonString(restaurantAddress.getZipCode()))
                        .append("country", new BsonString(restaurantAddress.getCountry())))
                //FIXME: lists must be lists
                .append("restaurantService", new BsonDocument());
                /*        .append("delivery", new BsonBoolean(restaurantService.isDelivery()))
                        .append("takeout", new BsonBoolean(restaurantService.isTakeout()))
                        .append("reserve", new BsonBoolean(restaurantService.isReserve())))
                .append("restaurantReviews", new BsonArray(Collections.singletonList(
                        new BsonDocument()
                                .append("reviewId", new BsonInt32(restaurantReviews.get(0).getId()))
                                .append("reviewerName", new BsonString(restaurantReviews.get(0).getReviewerName()))
                                .append("reviewerEmail", new BsonString(restaurantReviews.get(0).getReviewerEmail()))
                                .append("reviewerPhone", new BsonString(restaurantReviews.get(0).getReviewerPhone()))
                                .append("reviewerRating", new BsonInt32(restaurantReviews.get(0).getReviewerRating()))
                                .append("reviewerComment", new BsonString(restaurantReviews.get(0).getReviewerComment()))
                                .append("reviewerDate", new BsonDateTime(restaurantReviews.get(0).getReviewerDate().getTime()))
                )))
                .append("dishes", new BsonArray(Collections.singletonList(
                        new BsonDocument()
                                .append("dishId", new BsonInt32(dishes.get(0).getId()))
                                .append("dishName", new BsonString(dishes.get(0).getName()))
                                .append("dishDescription", new BsonString(dishes.get(0).getDescription()))
                                .append("dishPrice", new BsonDouble(dishes.get(0).getPrice()))
                                .append("dishType", new BsonString(dishes.get(0).getType()))
                                .append("*/

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
}

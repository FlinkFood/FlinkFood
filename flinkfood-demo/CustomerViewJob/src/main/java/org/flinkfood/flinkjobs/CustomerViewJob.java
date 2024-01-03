// Package declaration for the Flink job
package org.flinkfood.flinkjobs;

import org.flinkfood.FlinkEnvironments.CustomerEnvironment;

/**
 * This class, {@code CustomerViewJob}, represents a Flink job that creates the
 * Customer View.
 * The job retrieves data from Kafka topics related to customers and orders and
 * aggregates this data
 * to create a view of customers with their associated orders. The aggregated
 * view is then stored in a MongoDB collection.
 * 
 * <p>
 * FOR A MORE DETAILED EXPLANATION OF THE CODE, PLEASE REFER TO THE README FILE
 * "customerView.md"
 * </p>
 * 
 * <p>
 * The following steps outline the functionality of this job:
 * 1. Define necessary Kafka and MongoDB connection details.
 * 2. Set up the Flink execution environment and stream table environment.
 * 3. Register user-defined functions for aggregation.
 * 4. Define tables for customers and orders sourced from Kafka topics.
 * 5. Create a view table that represents an aggregated view of customers with
 * their orders.
 * 6. Execute the Flink job.
 * </p>
 * 
 * <p>
 * MAKE SURE THE CORRECT KAFKA AND MONGODB CONNECTION DETAILS ARE PROVIDED AND
 * THE DOCKER IS RUNNING AS STATED ON THE MD FILE
 * </p>
 *
 * @author Niccolo-Francioni
 * @version 1.0
 * @see org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
 * @see org.apache.flink.table.api.bridge.java.StreamTableEnvironment
 * @see org.apache.flink.table.api.TableConfig
 * @see org.apache.flink.types.Row
 * @see org.apache.flink.connector.mongodb.sink.MongoSink
 * @see org.apache.flink.connector.kafka.source.KafkaSource
 * @see org.apache.flink.streaming.api.datastream.DataStream
 * @see com.mongodb.client.model.Filters
 * @see com.mongodb.client.model.InsertOneModel
 * @see com.mongodb.client.model.ReplaceOneModel
 * @see com.mongodb.client.model.ReplaceOptions
 * @see org.flinkfood.schemas.customer.Customer
 * @see org.flinkfood.schemas.customer.KafkaCustomerSchema
 * @see org.flinkfood.schemas.order.KafkaOrderSchema
 * @see org.flinkfood.schemas.order.Order
 */

// Class declaration for the Flink job
public class CustomerViewJob {
        // Kafka and MongoDB connection details obtained from environment variables
        // private static final String MONGODB_URI = System.getenv("MONGODB_SERVER");
        private static final String MONGODB_URI = "mongodb://localhost:27017";

        // Main method where the Flink job is defined
        public static void main(String[] args) throws Exception {

                // Initialize environment
                var customerEnvironment = CustomerEnvironment.getInstance();

                // Register single view table
                customerEnvironment.getTableEnv().executeSql("CREATE TABLE CustomeView (\r\n" + //
                                "  id INT,\r\n" + //
                                "  first_name STRING,\r\n" + //
                                "  last_name STRING,\r\n" + //
                                "  orders ARRAY<row<id INT, name STRING, description STRING>>,\r\n" + //
                                "  payment_method ARRAY<row<id INT, name STRING>>,\r\n" + //
                                "  addresses ARRAY<row<id INT, street STRING,address_number STRING,zip_code INT,city STRING,province STRING,country STRING>>,\r\n"
                                + //
                                "  dish_review ARRAY<row<id INT, dish_id INT,name STRING,rating INT,description STRING>>,\r\n"
                                + //
                                "  restaurant_review ARRAY<row<id INT,restaurant_id INT, name STRING, rating INT, description STRING>>,\r\n"
                                + //
                                "  PRIMARY KEY (id) NOT ENFORCED\r\n" + //
                                ") WITH (\r\n" + //
                                "   'connector' = 'mongodb',\r\n" + //
                                "   'uri' = '" + MONGODB_URI + "',\r\n" + //
                                "   'database' = 'flinkfood',\r\n" + //
                                "   'collection' = 'users_sink'\r\n" + //
                                ");");

                // Execute query to aggregate data
                customerEnvironment.getTableEnv().executeSql(
                                "INSERT INTO CustomeView SELECT DISTINCT  c.id,c.first_name,c.last_name," +
                                                "(SELECT ARRAY_AGGR(ROW(o.id,o.name,o.description)) FROM Orders o WHERE o.customer_id = c.id),"
                                                + //
                                                "(SELECT ARRAY_AGGR(ROW(pm.id,pm.name)) FROM Payment_method pm WHERE pm.customer_id = c.id),"
                                                + //
                                                "(SELECT ARRAY_AGGR(ROW(ca.id,ca.street,ca.address_number,ca.zip_code,ca.city,ca.province,ca.country)) FROM Customer_address ca WHERE ca.customer_id = c.id),"
                                                + //
                                                "(SELECT ARRAY_AGGR(ROW(rd.id,rd.dish_id,d.name,rd.rating,rd.description)) FROM Review_dish rd  LEFT JOIN Dish d on rd.dish_id=d.id WHERE rd.customer_id = c.id ),"
                                                + //
                                                "(SELECT ARRAY_AGGR(ROW(rr.id,rr.restaurant_id,name,rr.rating,rr.description)) FROM Restaurant_review rr  LEFT JOIN Restaurant_info ri on rr.restaurant_id=ri.id WHERE rr.customer_id = c.id )"
                                                + //
                                                "FROM Customer c;");

                customerEnvironment.getEnv().execute("CustomerViewJob");
        }
}

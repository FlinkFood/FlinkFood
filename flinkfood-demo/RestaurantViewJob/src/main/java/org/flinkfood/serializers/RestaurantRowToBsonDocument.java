package org.flinkfood.serializers;

import java.util.*;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;

import javax.annotation.Nonnull;

// Remark: This is the worst code I've ever written. I'm sorry.
public class RestaurantRowToBsonDocument implements MongoSerializationSchema<Row> {
    // TODO: Source those from a config file
    final List<String> address_fields = List.of("street", "number", "city", "state", "country", "zipCode");
    final List<String> status_fields = List.of("takeAway", "delivery", "dineIn");
    final List<String> services_fields = List.of("parkingLots", "accessible", "childrenArea", "childrenFood");

    private void addFieldToDocument(BsonDocument document, String field_name, Object field) {
        if (field instanceof String) {
            document.append(field_name, new BsonString((String) field));
        } else if (field instanceof Integer) {
            document.append(field_name, new BsonInt32((Integer) field));
        } else if (field instanceof Long) {
            document.append(field_name, new BsonInt64((Long) field));
        } else if (field instanceof Double) {
            document.append(field_name, new BsonDouble((Double) field));
        } else if (field instanceof Boolean) {
            document.append(field_name, new BsonBoolean((Boolean) field));
        } else if (field instanceof Date) {
            document.append(field_name, new BsonDateTime(((Date) field).getTime()));
        } else {
            document.append(field_name, new BsonNull());
        }
    }

    private BsonDocument createSimpleRestaurantDocument(Row restaurant) {
        BsonDocument document = new BsonDocument();
        BsonDocument address_document = new BsonDocument();
        BsonDocument status_document = new BsonDocument();
        BsonDocument services_document = new BsonDocument();

        Set<String> field_names = restaurant.getFieldNames(true);
        assert field_names != null;
        for (String field_name : field_names) {
            if (address_fields.contains(field_name)) {
                this.addFieldToDocument(address_document, field_name, restaurant.getField(field_name));
            } else if (status_fields.contains(field_name)) {
                this.addFieldToDocument(status_document, field_name, restaurant.getField(field_name));
            } else if (services_fields.contains(field_name)) {
                this.addFieldToDocument(services_document, field_name, restaurant.getField(field_name));
            } else {
                this.addFieldToDocument(document, field_name, restaurant.getField(field_name));
            }
        }
        document.append("address", address_document);
        document.append("status", status_document);
        document.append("services", services_document);
        return document;
    }

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = this.createSimpleRestaurantDocument(row);
        BsonArray reviews = new BsonArray();
        document.append("reviews", reviews);
        return new InsertOneModel<>(document);
    }
}

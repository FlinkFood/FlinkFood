package org.flinkfood.serializers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

// Remark: This is the worst code I've ever written. I'm sorry.
public class RestaurantRowToBsonDocument implements MongoSerializationSchema<Row> {
    final List<String> address_fields = Arrays.asList("street", "number", "city", "state", "country", "zipCode");
    final List<String> status_fields = Arrays.asList("takeAway", "delivery", "dineIn");
    final List<String> services_fields = Arrays.asList("parkingLots", "accessible", "childrenArea", "childrenFood");

    private BsonDocument addFieldToDocument(BsonDocument documentChange, String field_name, Object field) {
        BsonDocument document = documentChange;
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
        return document;
    }

    private BsonDocument createSimpleRestaurantDocument(Row restaurant) {
        BsonDocument document = new BsonDocument();
        String[] field_names = restaurant.getFieldNames(true).stream().toArray(String[]::new);

        BsonDocument address_document = new BsonDocument();
        BsonDocument status_document = new BsonDocument();
        BsonDocument services_document = new BsonDocument();

        for (int i = 0; i < restaurant.getArity(); i++) {
            Object field = restaurant.getField(i);
            if(address_fields.contains(field_names[i])) {
                address_document = this.addFieldToDocument(address_document, field_names[i], field);
            }
            else if(status_fields.contains(field_names[i])) {
                status_document = this.addFieldToDocument(status_document, field_names[i], field);
            }
            else if(services_fields.contains(field_names[i])) {
                services_document = this.addFieldToDocument(services_document, field_names[i], field);
            } else {
                document = this.addFieldToDocument(document, field_names[i], field);
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

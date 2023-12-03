package org.flinkfood.serializers;

import java.util.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.conversions.Bson;

public class DishRowToBsonDocument implements MongoSerializationSchema<Row> {

    private void fieldName(BsonDocument document, String fieldName, Object field) {
        if (field instanceof String) {
            document.append(fieldName, new BsonString((String) field));
        } else if (field instanceof Integer) {
            document.append(fieldName, new BsonInt32((Integer) field));
        } else if (field instanceof Long) {
            document.append(fieldName, new BsonInt64((Long) field));
        } else if (field instanceof Double) {
            document.append(fieldName, new BsonDouble((Double) field));
        } else if (field instanceof Boolean) {
            document.append(fieldName, new BsonBoolean((Boolean) field));
        } else if (field instanceof Date) {
            document.append(fieldName, new BsonDateTime(((Date) field).getTime()));
        } else {
            document.append(fieldName, new BsonNull());
        }
    }

    private BsonDocument createDishDocument(Row dish) {
        BsonDocument document = new BsonDocument();
        Set<String> fieldNames = dish.getFieldNames(true);
        if (fieldNames != null) {
            for (String field_name : fieldNames) {
                this.fieldName(document, field_name, dish.getField(field_name));
            }
        }
        return document;
    }

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = this.createDishDocument(row);
        int idValue = document.getInt32(new String("dish_id")).getValue();
        Bson filter = Filters.eq("dish_id", idValue);

        return new ReplaceOneModel<>(filter, document, new ReplaceOptions().upsert(true));
    }
}

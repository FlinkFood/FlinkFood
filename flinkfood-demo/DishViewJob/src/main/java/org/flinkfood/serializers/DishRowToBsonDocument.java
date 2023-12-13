package org.flinkfood.serializers;

import java.util.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
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
import org.bson.conversions.Bson;
import org.flinkfood.schemas.restaurant.RestaurantInfo;

public class DishRowToBsonDocument implements MongoSerializationSchema<Row> {

    // BsonArray ingredient_documents = new BsonArray();
    final List<String> restaurant_info_fields = List.of("id", "name", "phone", "email", "cuisine_type", "price_range",
            "vat_code");
    final List<String> ingredients_fields = List.of("ingredient_id", "ingredient_name", "description", "carbs",
            "proteins", "fats", "fibers", "salt", "calories");

    private void addFieldToDocument(BsonDocument document, String fieldName, Object field) {
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

    private BsonDocument createDishDocument(Row dish, BsonArray ingredientDocuments) {
        BsonDocument dish_document = new BsonDocument();
        BsonDocument restaurant_info_document = new BsonDocument();
        BsonDocument ingredient_document = new BsonDocument();

        Set<String> fieldNames = dish.getFieldNames(true);
        if (fieldNames != null) {
            for (String field_name : fieldNames) {
                if (restaurant_info_fields.contains(field_name)) {
                    this.addFieldToDocument(restaurant_info_document, field_name, dish.getField(field_name));
                } else if (ingredients_fields.contains(field_name)) {
                    this.addFieldToDocument(ingredient_document, field_name, dish.getField(field_name));
                } else {
                    this.addFieldToDocument(dish_document, field_name, dish.getField(field_name));
                }
            }
        }

        dish_document.append("served_in", restaurant_info_document);
        ingredientDocuments.add(ingredient_document);
        return dish_document;
    }

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonArray ingredientDocuments = new BsonArray();
        BsonDocument document = this.createDishDocument(row, ingredientDocuments);
        int idValue = document.getInt32(new String("dish_id")).getValue();
        Bson filter = Filters.eq("dish_id", idValue);
        Bson insertDocument = Updates.setOnInsert(document);
        Bson updateDocument = Updates.addToSet("ingredients", ingredientDocuments.getValues().get(0));
        Bson updates = Updates.combine(insertDocument, updateDocument);
        return new UpdateOneModel<>(filter, updates, new UpdateOptions().upsert(true));
    }
}

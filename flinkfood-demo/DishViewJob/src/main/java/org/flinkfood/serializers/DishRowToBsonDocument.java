package org.flinkfood.serializers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.mongodb.shaded.org.bson.BsonNull;
import org.apache.flink.types.Row;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

public class DishRowToBsonDocument implements MongoSerializationSchema<Row> {

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        // Parse JSON string
        BsonDocument document = BsonDocument.parse(row.getField(0).toString());
        
        // Remove string array of ingredients and replace it with an array of documents
        BsonArray ingredientsArray = new BsonArray();
        document.getArray("ingredients").forEach((ingredient) -> {
            BsonDocument ingredientDocument = BsonDocument.parse(ingredient.asString().getValue());
            if (!ingredientDocument.get("ingredient_id").isNull()){
                System.out.println(ingredientDocument.get("ingredient_id"));
                ingredientsArray.add(ingredientDocument);
            }
        }); 
        document.put("ingredients", ingredientsArray);
        
        // Update existing document or insenrt a new one
        int idValue = document.getInt32(new String("dish_id")).getValue();
        Bson filter = Filters.eq("dish_id", idValue);
        return new ReplaceOneModel<>(filter, document, new ReplaceOptions().upsert(true));
    }
}

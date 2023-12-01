package org.flinkfood.serializers;

import java.util.*;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.BsonDocument;
import org.flinkfood._helper.InsertBsonField;
import org.flinkfood.viewFields.RestaurantViewAttribute;

// Remark: This is the worst code I've ever written. I'm sorry.
public class RestaurantRowToBsonDocument implements MongoSerializationSchema<Row>, InsertBsonField {
    // TODO: Source those from a config file
    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        return new InsertOneModel<>(createSimpleRestaurantDocument(row));
    }

    /**
     * Creates a simple restaurant document with the following structure:
     * @param restaurant is a row with all the fields of the aggregated view
     * @return document with annidated fields (not in array form!!)
     */
    private BsonDocument createSimpleRestaurantDocument(Row restaurant) {
        BsonDocument document = new BsonDocument();
        Set<String> field_names = restaurant.getFieldNames(true);

        Arrays.stream(RestaurantViewAttribute.values())
                .distinct().forEach(attribute ->
                {
                    var doc = new BsonDocument();
                    assert field_names != null;
                    field_names.stream().filter(attribute.getAttributes()::contains)
                            .forEach(field_name -> addFieldToDocument(doc, field_name, restaurant.getField(field_name)));
                    document.append(attribute.getName(), doc);
                }
        );
        return document;
    }

}

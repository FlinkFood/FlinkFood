package org.flinkfood.serializer;

import java.util.*;

import com.mongodb.client.model.InsertOneModel;
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

public class CustomerRowToBSON implements MongoSerializationSchema<Row> {
    
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

    private BsonDocument createSimpleCustomerDocument(Row customer) {
        BsonDocument document = new BsonDocument();

        Set<String> field_names = customer.getFieldNames(true);
        assert field_names != null;
        for (String field_name : field_names) {
                this.addFieldToDocument(document, field_name, customer.getField(field_name));
        }
        return document;
    }

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = this.createSimpleCustomerDocument(row);
        //BsonArray reviews = new BsonArray();
        //document.append("reviews", reviews);
        return new InsertOneModel<>(document);
    }
}

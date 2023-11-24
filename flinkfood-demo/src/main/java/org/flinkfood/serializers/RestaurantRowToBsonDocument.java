package org.flinkfood.serializers;

import java.util.Date;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.types.Row;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;

public class RestaurantRowToBsonDocument implements MongoSerializationSchema<Row> {

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = new BsonDocument();
        String[] field_names = row.getFieldNames(true).stream().toArray(String[]::new);

        for (int i = 0; i < row.getArity(); i++) {
            Object field = row.getField(i);
            if (field instanceof String) {
                document.append(field_names[i], new BsonString((String) field));
            } else if (field instanceof Integer) {
                document.append(field_names[i], new BsonInt32((Integer) field));
            } else if (field instanceof Long) {
                document.append(field_names[i], new BsonInt64((Long) field));
            } else if (field instanceof Double) {
                document.append(field_names[i], new BsonDouble((Double) field));
            } else if (field instanceof Boolean) {
                document.append(field_names[i], new BsonBoolean((Boolean) field));
            } else if (field instanceof Date) {
                document.append(field_names[i], new BsonDateTime(((Date) field).getTime()));
            } else {
                document.append(field_names[i], new BsonNull());
            }
        }
        return new InsertOneModel<>(document);
    }
}

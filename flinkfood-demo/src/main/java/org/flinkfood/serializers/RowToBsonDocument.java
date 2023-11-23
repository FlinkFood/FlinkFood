package org.flinkfood.serializers;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class RowToBsonDocument implements MongoSerializationSchema<Row> {

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = new BsonDocument();
        for (int i = 0; i < row.getArity(); i++) {
            document.append(String.format("field_%s", Integer.toString(i)), new BsonString(String.valueOf(row.getField(i))));
        }
        return new InsertOneModel<>(document);
    }
}

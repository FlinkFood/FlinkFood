package org.flinkfood.serializers;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.BsonDocument;
import org.bson.BsonString;

import java.util.Objects;
import java.util.Set;

public class RowToBsonDocument implements MongoSerializationSchema<Row> {

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = new BsonDocument();

        Objects.requireNonNull(
            row.getFieldNames(true))
                .forEach(field ->
                    document.append(field, new BsonString(String.valueOf(row.getField(field)))));

        // if we can get the _id field, with the value updated, we can replace the document.
        // also kafka's message newest timestamp will be used as the document's last update time.
        // new UpdateOneModel<>(new BsonDocument("_id", document.get("_id")), document);
        return new InsertOneModel<>(document);
    }
}

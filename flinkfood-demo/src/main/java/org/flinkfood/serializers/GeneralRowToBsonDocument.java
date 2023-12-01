package org.flinkfood.serializers;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.flink.connector.mongodb.sink.writer.context.MongoSinkContext;
import org.apache.flink.connector.mongodb.sink.writer.serializer.MongoSerializationSchema;
import org.apache.flink.types.Row;
import org.bson.*;
import org.flinkfood._helper.InsertBsonField;

import java.util.Objects;

public class GeneralRowToBsonDocument implements MongoSerializationSchema<Row>, InsertBsonField {

    @Override
    public WriteModel<BsonDocument> serialize(Row row, MongoSinkContext mongoSinkContext) {
        BsonDocument document = new BsonDocument();
        Objects.requireNonNull(row.getFieldNames(true))
                .forEach(field_name ->
                        addFieldToDocument(document, field_name, row.getField(field_name)));
        return new InsertOneModel<>(document);
    }

}
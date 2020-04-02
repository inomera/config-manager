package com.inomera.telco.commons.config.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Melek UZUN
 */
public class MongoConfigurationDao implements NoSqlConfigurationDao {

    private final MongoClient mongoClient;
    private final String dbName;
    private final String collectionName;
    private final String keyFieldName;
    private final String valueFieldName;
    private final Document query;

    public MongoConfigurationDao(MongoClient mongoClient, String dbName, String collectionName, String keyFieldName, String valueFieldName, Document query) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.keyFieldName = keyFieldName;
        this.valueFieldName = valueFieldName;
        this.query = query;
    }

    @Override
    public Map<String, String> findAllConfigurations() {
        final FindIterable<Document> documents = mongoClient.getDatabase(dbName).getCollection(collectionName).find(query);
        final Map<String, String> configurationMap = new HashMap<>();
        for (Document document : documents) {
            final String key = document.containsKey(keyFieldName) ? String.valueOf(document.get(keyFieldName)) : null;
            final String value = document.containsKey(valueFieldName) ? String.valueOf(document.get(valueFieldName)) : null;

            if (key != null && value != null) {
                configurationMap.put(key, value);
            }
        }
        return configurationMap;
    }

}

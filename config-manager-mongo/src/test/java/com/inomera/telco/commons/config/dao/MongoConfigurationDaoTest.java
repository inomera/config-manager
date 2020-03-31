package com.inomera.telco.commons.config.dao;

import com.mongodb.client.*;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Melek UZUN
 */
class MongoConfigurationDaoTest {

    private MongoClient mongoClient;

    private MongoConfigurationDao mongoConfigurationDao;

    private MongoDatabase mongoDatabase;

    private MongoCollection testCollection;

    private FindIterable findIterable;

    private MongoCursor mongoCursor;

    @BeforeEach
    void initialize() {
        mongoClient = mock(MongoClient.class);
        mongoDatabase = mock(MongoDatabase.class);
        testCollection = mock(MongoCollection.class);
        findIterable = mock(FindIterable.class);
        mongoCursor = mock(MongoCursor.class);
        Map<String, Object> query = new HashMap<>();
        query.put("one", '1');
        query.put("two", '2');
        query.put("three", '3');
        mongoConfigurationDao = new MongoConfigurationDao(mongoClient, "testDB", "testCollection",
                "key", "value", query);
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations(){
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(testCollection);
        when(testCollection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        Document setting1 = new Document();
        Document setting2 = new Document();
        Document setting3 = new Document();
        setting1.put("key","one");
        setting1.put("value","1");
        setting2.put("key", "two");
        setting2.put("value", "2");
        setting3.put("key", "three");
        setting3.put("value", "3");

        when(mongoCursor.next()).thenReturn(setting1).thenReturn(setting2).thenReturn(setting3);
        final Map<String, String> allConfigurations = mongoConfigurationDao.findAllConfigurations();

        assertEquals(3, allConfigurations.size());
        assertEquals("1", allConfigurations.get("one"));
        assertEquals("2", allConfigurations.get("two"));
        assertEquals("3", allConfigurations.get("three"));
    }

}
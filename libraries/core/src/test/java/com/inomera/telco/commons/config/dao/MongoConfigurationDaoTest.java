package com.inomera.telco.commons.config.dao;

import com.mongodb.client.*;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Melek UZUN
 */
@SuppressWarnings("unchecked")
class MongoConfigurationDaoTest {

    private MongoClient mongoClient;

    private MongoConfigurationDao mongoConfigurationDao;

    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> testCollection;

    private FindIterable<Document> findIterable;

    private MongoCursor<Document> mongoCursor;

    @BeforeEach
    void initialize() {
        mongoClient = mock(MongoClient.class);
        mongoDatabase = mock(MongoDatabase.class);
        testCollection = mock(MongoCollection.class);
        findIterable = mock(FindIterable.class);
        mongoCursor = mock(MongoCursor.class);
        Map<String, Object> query = new HashMap<>();
        query.put("application", "application");
        query.put("profile", "default");
        query.put("label", "master");
        mongoConfigurationDao = new MongoConfigurationDao(mongoClient, "testDB", "testCollection",
                "key", "value", new Document(query));
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations() {
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(testCollection);
        when(testCollection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.projection(any())).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        Document setting1 = new Document();
        Document setting2 = new Document();
        Document setting3 = new Document();
        setting1.put("key", "application");
        setting1.put("value", "application");
        setting2.put("key", "profile");
        setting2.put("value", "default");
        setting3.put("key", "label");
        setting3.put("value", "master");

        when(mongoCursor.next()).thenReturn(setting1).thenReturn(setting2).thenReturn(setting3);
        final Map<String, String> allConfigurations = mongoConfigurationDao.findAllConfigurations();

        assertEquals(3, allConfigurations.size());
        assertEquals("application", allConfigurations.get("application"));
        assertEquals("default", allConfigurations.get("profile"));
        assertEquals("master", allConfigurations.get("label"));
    }

    @Test
    @DisplayName("shouldFindUsingUserProvidedQuery")
    void shouldFindUsingUserProvidedQuery() {
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(anyString())).thenReturn(testCollection);
        when(testCollection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.projection(any())).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);

        Document setting1 = new Document();
        Document setting2 = new Document();
        Document setting3 = new Document();
        setting1.put("key", "application");
        setting1.put("value", "application");
        setting2.put("key", "profile");
        setting2.put("value", "default");
        setting3.put("key", "label");
        setting3.put("value", "master");

        when(mongoCursor.next()).thenReturn(setting1).thenReturn(setting2).thenReturn(setting3);
        mongoConfigurationDao.findAllConfigurations();

        verify(testCollection, times(1)).find(captor.capture());

        final Document actual = captor.getValue();
        assertEquals(3, actual.size());
        assertEquals("application", actual.get("application"));
        assertEquals("default", actual.get("profile"));
        assertEquals("master", actual.get("label"));
    }
}

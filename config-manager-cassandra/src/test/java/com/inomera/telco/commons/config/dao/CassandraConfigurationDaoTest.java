package com.inomera.telco.commons.config.dao;

import com.datastax.driver.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Melek UZUN
 */
class CassandraConfigurationDaoTest {

    private CassandraConfigurationDao cassandraConfigurationDao;

    private Session session;

    private PreparedStatement preparedStatement;

    private BoundStatement boundStatement;

    private ResultSet resultSet;

    private static final String SELECT = "select key, value from mps.app_settings where application = 'application'";

    @BeforeEach
    void initialize() {
        session = mock(Session.class);
        preparedStatement = mock(PreparedStatement.class);
        boundStatement = mock(BoundStatement.class);
        resultSet = mock(ResultSet.class);

        when(session.prepare(anyString())).thenReturn(preparedStatement);
        cassandraConfigurationDao = new CassandraConfigurationDaoImpl(session, SELECT);
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations() {
        when(preparedStatement.bind()).thenReturn(boundStatement);
        List<Row> rows = new ArrayList<>();
        rows.add(mockRow("key"));
        rows.add(mockRow("value"));
        when(session.execute(any(BoundStatement.class))).thenReturn(resultSet);
        when(resultSet.all()).thenReturn(rows);

        final Map<String, String> allConfigurations = cassandraConfigurationDao.findAllConfigurations();
        assertEquals(2, allConfigurations.size());
        assertEquals("key", allConfigurations.get("key"));
        assertEquals("value", allConfigurations.get("value"));

    }

    private Row mockRow(String value) {
        Row mockRow = mock(Row.class, value);
        when(mockRow.getString(anyInt())).thenReturn(value);
        return mockRow;
    }
}
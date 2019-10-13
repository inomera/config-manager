package com.inomera.telco.commons.config.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serdar Kuzucu
 */
@SuppressWarnings("ALL")
class NamedParameterJdbcTemplateConfigurationDaoImplTest {
    private static final String CREATE_TABLE = "CREATE TABLE CFG (KEY VARCHAR2(10 CHAR), VALUE VARCHAR2(10 CHAR), PRIMARY KEY(KEY));";
    private static final String SELECT = "SELECT KEY, VALUE FROM CFG;";
    private static final String INSERT = "INSERT INTO CFG (KEY, VALUE) VALUES (:configKey, :configValue);";
    private static final String UPDATE = "UPDATE CFG SET VALUE = :configValue WHERE KEY = :configKey;";
    private static final String DELETE = "DELETE FROM CFG WHERE KEY LIKE CONCAT(:prefix, '.%');";
    private static final String DELETE_ALL = "DELETE FROM CFG;";

    private NamedParameterJdbcTemplateConfigurationDaoImpl configurationDao;
    private JdbcDataSource dataSource;

    @BeforeEach
    void deleteTableAndInitDao() throws SQLException {
        this.dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        dataSource.getConnection().prepareStatement(CREATE_TABLE).execute();
        dataSource.getConnection().prepareStatement(DELETE_ALL).execute();
        this.configurationDao = new NamedParameterJdbcTemplateConfigurationDaoImpl(dataSource, SELECT, INSERT, UPDATE, DELETE);
    }

    @AfterEach
    void afterEach() throws SQLException {
        this.dataSource.getConnection().prepareStatement("SHUTDOWN;").execute();
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('a', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('b', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('c', '3');").execute();

        final Map<String, String> allConfigurations = configurationDao.findAllConfigurations();
        assertEquals(3, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
        assertEquals("3", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should insert new configuration")
    void shouldInsertNewConfiguration() throws SQLException {
        configurationDao.insertNewConfiguration("testKey", "testValue");

        final ResultSet resultSet = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'testKey'").executeQuery();
        assertTrue(resultSet.next());
        assertEquals("testValue", resultSet.getString(1));
    }

    @Test
    @DisplayName("Should update existing configuration")
    void shouldUpdateExistingConfiguration() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('a', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('b', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('c', '3');").execute();

        final int updatedRowCount = configurationDao.updateConfiguration("b", "5");
        assertEquals(1, updatedRowCount);

        final ResultSet resultSetA = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'a'").executeQuery();
        assertTrue(resultSetA.next());
        assertEquals("1", resultSetA.getString(1));

        final ResultSet resultSetB = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'b'").executeQuery();
        assertTrue(resultSetB.next());
        assertEquals("5", resultSetB.getString(1));

        final ResultSet resultSetC = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'c'").executeQuery();
        assertTrue(resultSetC.next());
        assertEquals("3", resultSetC.getString(1));
    }

    @Test
    @DisplayName("Should return zero when nothing is updated")
    void shouldReturnZeroWhenNothingIsUpdated() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('a', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('b', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('c', '3');").execute();

        final int updatedRowCount = configurationDao.updateConfiguration("d", "5");
        assertEquals(0, updatedRowCount);

        final ResultSet resultSetA = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'a'").executeQuery();
        assertTrue(resultSetA.next());
        assertEquals("1", resultSetA.getString(1));

        final ResultSet resultSetB = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'b'").executeQuery();
        assertTrue(resultSetB.next());
        assertEquals("2", resultSetB.getString(1));

        final ResultSet resultSetC = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'c'").executeQuery();
        assertTrue(resultSetC.next());
        assertEquals("3", resultSetC.getString(1));

        final ResultSet resultSetD = dataSource.getConnection().prepareStatement("SELECT VALUE FROM CFG WHERE KEY = 'd'").executeQuery();
        assertFalse(resultSetD.next());
    }

    @Test
    @DisplayName("Should delete all configurations by prefix")
    void shouldDeleteAllConfigurationsByPrefixCorrectly() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('my.0', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('my.1', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('my.2', '3');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (KEY, VALUE) VALUES ('no.3', '4');").execute();

        configurationDao.deleteConfigurationsByPrefix("my");

        final ResultSet resultSet = dataSource.getConnection().prepareStatement("SELECT COUNT(*) FROM CFG WHERE KEY LIKE 'my%';").executeQuery();
        assertTrue(resultSet.next());
        assertEquals(0, resultSet.getInt(1));
    }
}

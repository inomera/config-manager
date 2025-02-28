package com.inomera.telco.commons.config.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Serdar Kuzucu
 */
@SuppressWarnings("ALL")
class SqlDataSourceConfigurationDaoImplTest {
    private static final String CREATE_TABLE = "CREATE TABLE CFG (\"KEY\" VARCHAR2(10 CHAR), \"VALUE\" VARCHAR2(10 CHAR), PRIMARY KEY(\"KEY\", \"VALUE\"));";
    private static final String SELECT = "SELECT \"KEY\", \"VALUE\" FROM CFG order by \"VALUE\" ASC;";
    private static final String INSERT = "INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES (:configKey, :configValue);";
    private static final String UPDATE = "UPDATE CFG SET \"VALUE\" = :configValue WHERE \"KEY\" = :configKey;";
    private static final String DELETE = "DELETE FROM CFG WHERE \"KEY\" LIKE CONCAT(:prefix, '.%');";
    private static final String DELETE_ALL = "DELETE FROM CFG;";

    private SqlDataSourceConfigurationDaoImpl configurationDao;
    private JdbcDataSource dataSource;

    @BeforeEach
    void deleteTableAndInitDao() throws SQLException {
        this.dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        dataSource.getConnection().prepareStatement(CREATE_TABLE).execute();
        dataSource.getConnection().prepareStatement(DELETE_ALL).execute();
        this.configurationDao = new SqlDataSourceConfigurationDaoImpl(dataSource, SELECT);
    }

    @AfterEach
    void afterEach() throws SQLException {
        this.dataSource.getConnection().prepareStatement("SHUTDOWN;").execute();
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('a', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('b', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('c', '3');").execute();

        final Map<String, String> allConfigurations = configurationDao.findAllConfigurations();
        assertEquals(3, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
        assertEquals("3", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should not throw exception for duplicate keys")
    void shouldFetchAllConfigurationsWithoutThrowingDuplicateKeyException() throws SQLException {
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('a', '1');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('b', '2');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('c', '3');").execute();
        dataSource.getConnection().prepareStatement("INSERT INTO CFG (\"KEY\", \"VALUE\") VALUES ('a', '5');").execute();

        final Map<String, String> allConfigurations = configurationDao.findAllConfigurations();
        assertEquals(3, allConfigurations.size());
        assertEquals("5", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
        assertEquals("3", allConfigurations.get("c"));
    }
}

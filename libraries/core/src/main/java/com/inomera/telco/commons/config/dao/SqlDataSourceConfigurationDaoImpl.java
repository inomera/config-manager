package com.inomera.telco.commons.config.dao;

import com.inomera.telco.commons.config.fault.ConfigurationManagerRuntimeException;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses javax.sql.DataSource abstraction to fetch configurations from a relational database
 *
 * @author Serdar Kuzucu
 */
public class SqlDataSourceConfigurationDaoImpl implements ConfigurationDao {
    private final DataSource dataSource;
    private final String selectSql;

    /**
     * Creates a new SqlDataSourceConfigurationDaoImpl
     *
     * @param dataSource datasource to connect for configuration
     * @param selectSql  sql to select configuration key and value in order.
     *                   Example: SELECT SETTING_NAME, SETTING_VALUE FROM APP_SETTINGS
     */
    public SqlDataSourceConfigurationDaoImpl(DataSource dataSource, String selectSql) {
        this.dataSource = dataSource;
        this.selectSql = selectSql;
    }

    public Map<String, String> findAllConfigurations() {
        final Map<String, String> configurations = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                final String key = StringUtils.defaultIfBlank(resultSet.getString(1), StringUtils.EMPTY);
                final String value = StringUtils.defaultIfBlank(resultSet.getString(2), StringUtils.EMPTY);
                configurations.put(key, value);
            }
        } catch (Exception e) {
            throw new ConfigurationManagerRuntimeException(e);
        }

        return configurations;
    }
}

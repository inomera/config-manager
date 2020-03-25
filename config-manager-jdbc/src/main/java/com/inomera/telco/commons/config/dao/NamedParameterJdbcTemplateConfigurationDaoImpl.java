package com.inomera.telco.commons.config.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses Spring Framework's NamedParameterJdbcTemplate to fetch/update/insert configurations in a relational database
 *
 * @author Serdar Kuzucu
 */
public class NamedParameterJdbcTemplateConfigurationDaoImpl implements ConfigurationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String selectSql;
    private final String insertSql;
    private final String updateSql;
    private final String deleteByPrefixSql;

    /**
     * Creates a new NamedParameterJdbcTemplateConfigurationDaoImpl
     *
     * @param dataSource        datasource to connect for configuration
     * @param selectSql         sql to select configuration key and value in order.
     *                          Example: SELECT SETTING_NAME, SETTING_VALUE FROM APP_SETTINGS
     * @param insertSql         sql to insert new configuration key & value. SQL should contain :configKey and :configValue parameters.
     *                          Example: INSERT INTO APP_SETTINGS (ID, SETTING_KEY, SETTING_VALUE) VALUES (SEQ_APP_SETTINGS_ID.nextVal, :configKey, :configValue)
     * @param updateSql         sql to update an existing configuration key's value. SQL should contain :configKey and :configValue parameters.
     * @param deleteByPrefixSql sql to delete existing configurations by key prefix. Should contain :prefix
     *                          Example: DELETE FROM APP_SETTINGS WHERE SETTING_KEY LIKE CONCAT(:prefix, '.%')
     */
    public NamedParameterJdbcTemplateConfigurationDaoImpl(DataSource dataSource, String selectSql, String insertSql, String updateSql, String deleteByPrefixSql) {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        this.jdbcTemplate = new NamedParameterJdbcTemplate(transactionManager.getDataSource());
        this.deleteByPrefixSql = deleteByPrefixSql;
        this.selectSql = selectSql;
        this.insertSql = insertSql;
        this.updateSql = updateSql;
    }

    public Map<String, String> findAllConfigurations() {
        final RowMapper<String[]> rowMapper = (rs, rowNum) -> {
            final String settingName = StringUtils.defaultIfEmpty(rs.getString(1), StringUtils.EMPTY);
            final String settingValue = StringUtils.defaultIfEmpty(rs.getString(2), StringUtils.EMPTY);
            return new String[]{settingName, settingValue};
        };
        final List<String[]> results = jdbcTemplate.query(selectSql, rowMapper);
        return results.stream().collect(Collectors.toMap(row -> row[0], row -> row[1], (firstValue, secondValue) -> secondValue));
    }

    public void insertNewConfiguration(String key, String value) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("configKey", key).addValue("configValue", value);
        jdbcTemplate.update(insertSql, parameters);
    }

    public int updateConfiguration(String key, String value) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("configKey", key).addValue("configValue", value);
        return jdbcTemplate.update(updateSql, parameters);
    }

    @Override
    public void deleteConfigurationsByPrefix(String prefix) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("prefix", prefix);
        jdbcTemplate.update(deleteByPrefixSql, parameters);
    }
}

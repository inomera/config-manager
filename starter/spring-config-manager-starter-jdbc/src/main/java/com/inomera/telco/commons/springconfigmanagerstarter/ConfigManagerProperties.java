package com.inomera.telco.commons.springconfigmanagerstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Serdar Kuzucu
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "config-manager")
public class ConfigManagerProperties {
    private static final String DEFAULT_SELECT_SQL = "SELECT SETTING_NAME, SETTING_VALUE FROM APP_SETTINGS";
    private static final String DEFAULT_INSERT_SQL = "INSERT INTO APP_SETTINGS (ID, SETTING_KEY, SETTING_VALUE) VALUES (SEQ_APP_SETTINGS_ID.nextVal, ?, ?)";
    private static final String DEFAULT_UPDATE_SQL = "UPDATE APP_SETTINGS SET SETTING_VALUE = ? WHERE SETTING_NAME = ?";
    private static final String DEFAULT_DELETE_BY_PREFIX_SQL = "DELETE FROM APP_SETTINGS WHERE SETTING_KEY LIKE CONCAT(:prefix, '.%')";

    private int valueColumnLength = 255;
    private boolean enabled = false;
    private SqlStatements sql = new SqlStatements();
    private int reloadPeriodInMilliseconds;
    private String reloadCronExpression;
    private DataSourceProperties dataSource;

    @Getter
    @Setter
    public static class SqlStatements {
        private String select = DEFAULT_SELECT_SQL;
        private String insert = DEFAULT_INSERT_SQL;
        private String update = DEFAULT_UPDATE_SQL;
        private String deleteByPrefix = DEFAULT_DELETE_BY_PREFIX_SQL;
    }

    @Getter
    @Setter
    public static class DataSourceProperties {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
    }
}

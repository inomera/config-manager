package com.inomera.telco.commons.config.spring.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Serdar Kuzucu
 */
public class ConfigManagerEnabledAndCassandraSourceCondition extends AllNestedConditions {
    public ConfigManagerEnabledAndCassandraSourceCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
    static class OnConfigurationManagerEnabled {
    }

    @ConditionalOnProperty(value = "config-manager.source", havingValue = "cassandra")
    static class OnJdbcSourceEnabled {
    }
}

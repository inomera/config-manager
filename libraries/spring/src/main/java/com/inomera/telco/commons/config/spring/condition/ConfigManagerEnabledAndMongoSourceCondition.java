package com.inomera.telco.commons.config.spring.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Serdar Kuzucu
 */
public class ConfigManagerEnabledAndMongoSourceCondition extends AllNestedConditions {
    public ConfigManagerEnabledAndMongoSourceCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
    static class OnConfigurationManagerEnabled {
    }

    @ConditionalOnProperty(value = "config-manager.source", havingValue = "mongo")
    static class OnJdbcSourceEnabled {
    }
}

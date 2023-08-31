package com.inomera.telco.commons.config.spring.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class ConfigManagerEnabledAndRedisSourceCondition extends AllNestedConditions {
    public ConfigManagerEnabledAndRedisSourceCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
    static class OnConfigurationManagerEnabled {
    }

    @ConditionalOnProperty(value = "config-manager.source", havingValue = "redis")
    static class OnRedisSourceEnabled {
    }
}

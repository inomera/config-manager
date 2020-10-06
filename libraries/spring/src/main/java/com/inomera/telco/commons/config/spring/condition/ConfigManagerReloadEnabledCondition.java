package com.inomera.telco.commons.config.spring.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Serdar Kuzucu
 */
public class ConfigManagerReloadEnabledCondition extends AllNestedConditions {
    public ConfigManagerReloadEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
    static class OnConfigurationManagerEnabled {
    }

    @ConditionalOnProperty(value = "config-manager.reload.enabled", havingValue = "true")
    static class OnAutoConfigReloadEnabled {
    }
}

package com.inomera.telco.commons.config.spring.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author Serdar Kuzucu
 */
public class ConfigManagerEncryptionEnabledCondition extends AllNestedConditions {
    public ConfigManagerEncryptionEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
    static class OnConfigurationManagerEnabled {
    }

    @ConditionalOnProperty(value = "config-manager.encryption.enabled", havingValue = "true")
    static class OnEncryptionEnabled {
    }
}

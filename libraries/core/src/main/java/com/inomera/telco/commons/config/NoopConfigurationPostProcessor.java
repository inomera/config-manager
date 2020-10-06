package com.inomera.telco.commons.config;

/**
 * @author Serdar Kuzucu
 */
public class NoopConfigurationPostProcessor implements ConfigurationPostProcessor {
    @Override
    public String postProcessConfiguration(String key, String value) {
        return value;
    }
}

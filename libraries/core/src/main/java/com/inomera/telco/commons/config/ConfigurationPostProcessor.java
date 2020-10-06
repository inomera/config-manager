package com.inomera.telco.commons.config;

/**
 * @author Serdar Kuzucu
 */
public interface ConfigurationPostProcessor {
    String postProcessConfiguration(String key, String value);
}

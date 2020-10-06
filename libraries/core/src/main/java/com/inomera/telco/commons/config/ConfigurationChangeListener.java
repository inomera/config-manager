package com.inomera.telco.commons.config;

/**
 * @author Melek UZUN
 */
public interface ConfigurationChangeListener {
    void onChange(String configKey, String oldValue, String newValue);

}

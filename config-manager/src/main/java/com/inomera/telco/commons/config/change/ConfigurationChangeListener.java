package com.inomera.telco.commons.config.change;

/**
 * @author Melek UZUN
 */
public interface ConfigurationChangeListener {
    void onChange(String oldValue, String newValue);

}

package com.inomera.telco.commons.config.dao;

import java.util.Map;

/**
 * @author Serdar Kuzucu
 */
public interface ConfigurationDao {
    Map<String, String> findAllConfigurations();

    void insertNewConfiguration(String key, String value);

    int updateConfiguration(String key, String value);

    void deleteConfigurationsByPrefix(String prefix);
}

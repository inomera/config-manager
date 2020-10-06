package com.inomera.telco.commons.config.dao;

import java.util.Map;

/**
 * @author Serdar Kuzucu
 */
public interface ConfigurationDao {
    Map<String, String> findAllConfigurations();
}

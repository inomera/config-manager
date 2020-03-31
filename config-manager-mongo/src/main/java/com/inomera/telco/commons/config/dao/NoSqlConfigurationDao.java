package com.inomera.telco.commons.config.dao;

import java.util.Map;

/**
 * @author Melek UZUN
 */
public interface NoSqlConfigurationDao {
    Map<String, String> findAllConfigurations();
}

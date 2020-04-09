package com.inomera.telco.commons.config.dao;

import java.util.Map;

/**
 * @author Melek UZUN
 */
public interface CassandraConfigurationDao {
    Map<String, String> findAllConfigurations();
}

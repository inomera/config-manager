package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.CassandraConfigurationDao;

import java.util.Map;

/**
 * @author Melek UZUN
 */
public class CassandraConfigurationFetcherService implements ConfigurationFetcherService {

    private final CassandraConfigurationDao cassandraConfigurationDao;

    public CassandraConfigurationFetcherService(CassandraConfigurationDao cassandraConfigurationDao) {
        this.cassandraConfigurationDao = cassandraConfigurationDao;
    }

    @Override
    public Map<String, String> fetchConfiguration() {
        return cassandraConfigurationDao.findAllConfigurations();
    }

}

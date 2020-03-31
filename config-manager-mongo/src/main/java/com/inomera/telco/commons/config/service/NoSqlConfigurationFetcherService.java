package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.NoSqlConfigurationDao;

import java.util.Map;

/**
 * @author Melek UZUN
 */
public class NoSqlConfigurationFetcherService implements ConfigurationFetcherService {

    private final NoSqlConfigurationDao noSqlConfigurationDao;

    public NoSqlConfigurationFetcherService(NoSqlConfigurationDao noSqlConfigurationDao) {
        this.noSqlConfigurationDao = noSqlConfigurationDao;
    }

    @Override
    public Map<String, String> fetchConfiguration() {
        return noSqlConfigurationDao.findAllConfigurations();
    }

}

package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author Serdar Kuzucu
 */
@RequiredArgsConstructor
public class ConfigurationDaoConfigurationFetcherService implements ConfigurationFetcherService {

    private final ConfigurationDao configurationDao;

    @Override
    public Map<String, String> fetchConfiguration() {
        return configurationDao.findAllConfigurations();
    }
}

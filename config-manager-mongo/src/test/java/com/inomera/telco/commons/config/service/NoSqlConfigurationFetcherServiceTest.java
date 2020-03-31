package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.NoSqlConfigurationDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Melek UZUN
 */
@ExtendWith(MockitoExtension.class)
class NoSqlConfigurationFetcherServiceTest {

    @InjectMocks
    NoSqlConfigurationFetcherService noSqlConfigurationFetcherService;

    @Mock
    NoSqlConfigurationDao noSqlConfigurationDao;

    @Test
    @DisplayName("Should return all configuration coming from NoSqlConfigurationDAO")
    void shouldReturnAllConfigurationComingFromNoSqlConfigurationDAO() {
        final Map<String, String> configurationFromDao = new HashMap<>();
        configurationFromDao.put("a", "1");
        configurationFromDao.put("b", "2");
        configurationFromDao.put("c", "3");
        Mockito.when(noSqlConfigurationDao.findAllConfigurations()).thenReturn(configurationFromDao);
        final Map<String, String> configuration = noSqlConfigurationFetcherService.fetchConfiguration();

        assertEquals(configurationFromDao, configuration);
    }

}
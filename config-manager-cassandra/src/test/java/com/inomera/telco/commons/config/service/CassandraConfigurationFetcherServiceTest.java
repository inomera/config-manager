package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.CassandraConfigurationDao;
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
class CassandraConfigurationFetcherServiceTest {

    @InjectMocks
    private CassandraConfigurationFetcherService cassandraConfigurationFetcherService;

    @Mock
    private CassandraConfigurationDao cassandraConfigurationDao;

    @Test
    @DisplayName("Should return all configuration coming from CassandraConfigurationDAO")
    void shouldReturnAllConfigurationComingFromCassandraConfigurationDAO() {
        final Map<String, String> configurationFromDao = new HashMap<>();
        configurationFromDao.put("a", "1");
        configurationFromDao.put("b", "2");
        configurationFromDao.put("c", "3");
        Mockito.when(cassandraConfigurationDao.findAllConfigurations()).thenReturn(configurationFromDao);
        final Map<String, String> configuration = cassandraConfigurationFetcherService.fetchConfiguration();

        assertEquals(configurationFromDao, configuration);
    }

}
package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class ConfigurationDaoConfigurationFetcherServiceTest {
    @InjectMocks
    private ConfigurationDaoConfigurationFetcherService service;

    @Mock
    private ConfigurationDao configurationDao;

    @Test
    @DisplayName("Should return all configuration coming from configurationDao")
    void shouldReturnAllConfigurationComingFromConfigurationDao() {
        final Map<String, String> configurationFromDao = new HashMap<>();
        configurationFromDao.put("a", "1");
        configurationFromDao.put("b", "2");
        configurationFromDao.put("c", "3");
        when(configurationDao.findAllConfigurations()).thenReturn(configurationFromDao);

        final Map<String, String> configuration = service.fetchConfiguration();
        assertEquals(configurationFromDao, configuration);
    }
}

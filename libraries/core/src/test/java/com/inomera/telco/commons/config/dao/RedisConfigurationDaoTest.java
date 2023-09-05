package com.inomera.telco.commons.config.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.nullable;

@ExtendWith(MockitoExtension.class)
class RedisConfigurationDaoTest {
    @InjectMocks
    private RedisConfigurationDao redisConfigurationDao;

    @Mock
    private RedissonClient redisson;

    @Mock
    private RMap<String, String> redisConfigMap;

    @BeforeEach
    public void setUp() {
        final Map<String, String> configMap = new HashMap<>();
        configMap.put("first-key", "first-value");
        configMap.put("second-key", "second-value");
        doReturn(redisConfigMap).when(redisson).getMap(nullable(String.class));
        doReturn(configMap).when(redisConfigMap).readAllMap();
    }

    @Test
    @DisplayName("Should fetch all configurations")
    void shouldFetchAllConfigurations() {
        final Map<String, String> configurations = redisConfigurationDao.findAllConfigurations();

        final String firstValue = configurations.get("first-key");
        assertEquals("first-value", firstValue);

        final String secondValue = configurations.get("second-key");
        assertEquals("second-value", secondValue);

        final String thirdValue = configurations.get("third-key");
        assertNull(thirdValue);
    }
}

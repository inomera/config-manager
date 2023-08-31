package com.inomera.telco.commons.config.dao;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;

@RequiredArgsConstructor
public class RedisConfigurationDao implements ConfigurationDao {
    private final String mapKey;
    private final RedissonClient redisson;

    @Override
    public Map<String, String> findAllConfigurations() {
        final RMap<String, String> configurations = redisson.getMap(mapKey);
        return configurations.readAllMap();
    }
}

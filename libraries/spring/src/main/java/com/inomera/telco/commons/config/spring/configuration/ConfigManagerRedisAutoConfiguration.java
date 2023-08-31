package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.config.dao.RedisConfigurationDao;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEnabledAndRedisSourceCondition;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.inomera.telco.commons.config.spring.BeanNames.*;

@Slf4j
@Configuration
@Conditional(ConfigManagerEnabledAndRedisSourceCondition.class)
public class ConfigManagerRedisAutoConfiguration {

    @Bean(name = BEAN_CM_REDISSON_CONFIG)
    @ConditionalOnMissingBean(name = BEAN_CM_REDISSON_CONFIG)
    public Config redissonConfig(@Value("${config-manager.redis.redisson.config}") String config) {
        try {
            return Config.fromYAML(config);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't parse redisson config", e);
        }
    }

    @Bean(name = BEAN_CM_REDISSON_CLIENT)
    @ConditionalOnBean(name = BEAN_CM_REDISSON_CONFIG)
    @ConditionalOnMissingBean(name = BEAN_CM_REDISSON_CLIENT)
    public RedissonClient redisson(@Qualifier(BEAN_CM_REDISSON_CONFIG) Config redissonConfig) {
        return Redisson.create(redissonConfig);
    }

    @Bean(name = BEAN_CM_CONFIGURATION_DAO)
    @ConditionalOnBean(name = BEAN_CM_REDISSON_CLIENT)
    @ConditionalOnMissingBean(name = BEAN_CM_CONFIGURATION_DAO)
    public ConfigurationDao configurationDao(@Value("${config-manager.redis.redisson.map-key}") String mapKey,
                                             @Qualifier(BEAN_CM_REDISSON_CLIENT) RedissonClient redissonClient) {
        return new RedisConfigurationDao(mapKey, redissonClient);
    }
}

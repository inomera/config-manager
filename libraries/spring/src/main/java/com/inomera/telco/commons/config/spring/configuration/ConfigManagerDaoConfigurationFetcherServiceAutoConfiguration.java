package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.config.service.ConfigurationDaoConfigurationFetcherService;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_CONFIGURATION_DAO;
import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_FETCHER_SERVICE;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
public class ConfigManagerDaoConfigurationFetcherServiceAutoConfiguration {

    @Bean(name = BEAN_CM_FETCHER_SERVICE)
    @ConditionalOnMissingBean(name = BEAN_CM_FETCHER_SERVICE)
    @ConditionalOnBean(name = BEAN_CM_CONFIGURATION_DAO)
    public ConfigurationFetcherService configurationFetcherService(
            @Qualifier(BEAN_CM_CONFIGURATION_DAO) ConfigurationDao configurationDao) {
        return new ConfigurationDaoConfigurationFetcherService(configurationDao);
    }
}

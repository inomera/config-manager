package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.ConcurrentHashMapConfigurationHolder;
import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_FETCHER_SERVICE;
import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CONFIGURATION_HOLDER;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@ConditionalOnProperty(value = "config-manager.enabled", havingValue = "true")
@ConditionalOnClass(ConfigurationHolder.class)
@Import({ConfigManagerCassandraAutoConfiguration.class,
        ConfigManagerMongoAutoConfiguration.class,
        ConfigManagerJdbcAutoConfiguration.class,
        ConfigManagerDaoConfigurationFetcherServiceAutoConfiguration.class,
        ConfigManagerPropertiesAutoConfiguration.class,
        ConfigurationReloadAutoConfiguration.class})
public class ConfigurationHolderAutoConfiguration {

    @Bean(name = BEAN_CONFIGURATION_HOLDER)
    @ConditionalOnMissingBean
    public ConfigurationHolder configurationHolder(
            @Qualifier(BEAN_CM_FETCHER_SERVICE) ConfigurationFetcherService configurationFetcherService) {
        // TODO encryption
        return new ConcurrentHashMapConfigurationHolder(configurationFetcherService);
    }

}

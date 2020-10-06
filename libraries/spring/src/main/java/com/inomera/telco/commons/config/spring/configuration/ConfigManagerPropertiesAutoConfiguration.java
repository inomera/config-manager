package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import com.inomera.telco.commons.config.service.InputStreamSupplier;
import com.inomera.telco.commons.config.service.PropertyResourceConfigurationFetcherService;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEnabledAndPropertiesSourceCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_FETCHER_SERVICE;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@Conditional(ConfigManagerEnabledAndPropertiesSourceCondition.class)
public class ConfigManagerPropertiesAutoConfiguration {

    @Bean(name = BEAN_CM_FETCHER_SERVICE)
    @ConditionalOnMissingBean(name = BEAN_CM_FETCHER_SERVICE)
    public ConfigurationFetcherService configurationFetcherService(
            ApplicationContext applicationContext,
            @Value("${config-manager.properties.property-files}") List<String> propertyFiles) {
        return new PropertyResourceConfigurationFetcherService(false, getResources(applicationContext, propertyFiles));
    }

    private InputStreamSupplier[] getResources(ApplicationContext applicationContext, List<String> propertyFiles) {
        return propertyFiles.stream()
                .map(applicationContext::getResource)
                .map(ResourceInputStreamSupplier::new)
                .toArray(InputStreamSupplier[]::new);
    }

    @RequiredArgsConstructor
    private static class ResourceInputStreamSupplier implements InputStreamSupplier {
        private final Resource resource;

        @Override
        public InputStream get() throws Exception {
            return resource.getInputStream();
        }
    }
}

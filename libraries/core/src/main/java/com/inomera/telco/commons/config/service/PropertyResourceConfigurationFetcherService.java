package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.fault.ConfigurationManagerRuntimeException;
import com.inomera.telco.commons.lang.function.ThrowableSupplier;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;

/**
 * Reads all configuration from given Resources in the given order.
 * Configurations on the last Resource have the highest precedence.
 *
 * @author Serdar Kuzucu
 */
@Slf4j
public class PropertyResourceConfigurationFetcherService implements ConfigurationFetcherService {
    private final boolean failOnResourceErrors;
    private final InputStreamSupplier[] resources;

    /**
     * Creates a new PropertyResourceConfigurationFetcherService
     *
     * @param failOnResourceErrors if true, fetchConfiguration method throws RuntimeException when resource cannot be read.
     * @param resources            resources to be read, in order.
     */
    public PropertyResourceConfigurationFetcherService(boolean failOnResourceErrors, InputStreamSupplier... resources) {
        this.failOnResourceErrors = failOnResourceErrors;
        this.resources = Objects.requireNonNull(resources);
    }

    @Override
    public Map<String, String> fetchConfiguration() {
        final Map<String, String> configurations = new HashMap<>();

        for (int i = 0, resourcesLength = resources.length; i < resourcesLength; i++) {
            try {
                final ThrowableSupplier<InputStream> resource = resources[i];
                final InputStream inputStream = resource.get();
                final Properties properties = new Properties();
                properties.load(inputStream);
                inputStream.close();
                putPropertiesIntoMap(configurations, properties);
            } catch (Exception e) {
                LOG.error("Error reading properties from {}. resource: {}", i, e.getMessage(), e);
                if (failOnResourceErrors) {
                    throw new ConfigurationManagerRuntimeException(e);
                }
            }
        }

        return configurations;
    }

    private void putPropertiesIntoMap(Map<String, String> configurations, Properties properties) {
        final Set<String> propertyNames = properties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            final Object property = properties.get(propertyName);
            final String propertyValue = String.valueOf(property);
            LOG.debug("loadAllConfigurations::property={}::value={}", propertyName, propertyValue);
            configurations.put(propertyName, propertyValue);
        }
    }
}

package com.inomera.telco.commons.config.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Reads all configuration from given Resources in the given order.
 * Configurations on the last Resource have the highest precedence.
 *
 * @author Serdar Kuzucu
 */
public class PropertyResourceConfigurationFetcherService implements ConfigurationFetcherService {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyResourceConfigurationFetcherService.class);

    private final boolean failOnResourceErrors;
    private final Resource[] resources;

    /**
     * Creates a new PropertyResourceConfigurationFetcherService
     *
     * @param failOnResourceErrors if true, fetchConfiguration method throws RuntimeException when resource cannot be read.
     * @param resources            resources to be read, in order.
     */
    public PropertyResourceConfigurationFetcherService(boolean failOnResourceErrors, Resource... resources) {
        this.failOnResourceErrors = failOnResourceErrors;
        this.resources = Objects.requireNonNull(resources);
    }

    @Override
    public Map<String, String> fetchConfiguration() {
        final Map<String, String> configurations = new HashMap<>();

        Arrays.stream(resources)
                .map(this::loadFromResource)
                .forEach(propertiesToMapConsumer(configurations));

        return configurations;
    }

    private Properties loadFromResource(Resource resource) {
        try {
            if (resource == null) {
                LOG.warn("Resource is null!");
                return new Properties();
            }

            if (!resource.exists()) {
                LOG.warn("Resource does not exist!");
                return new Properties();
            }

            return PropertiesLoaderUtils.loadProperties(resource);
        } catch (Exception e) {
            LOG.error("Error reading properties {} from resource: {}", resource, e.getMessage(), e);
            if (failOnResourceErrors) {
                throw new PropertyFileReadException("Failed to read resource: " + e.getMessage(), e);
            }
            return new Properties();
        }
    }

    private Consumer<Properties> propertiesToMapConsumer(Map<String, String> map) {
        return properties -> putPropertiesIntoMap(map, properties);
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

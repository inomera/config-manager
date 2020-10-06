package com.inomera.telco.commons.config;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Serdar Kuzucu
 */
@RequiredArgsConstructor
public class CompositeConfigurationPostProcessor implements ConfigurationPostProcessor {
    private final List<ConfigurationPostProcessor> configurationPostProcessors;

    public CompositeConfigurationPostProcessor() {
        this(ImmutableList.of());
    }

    @Override
    public String postProcessConfiguration(String key, String value) {
        for (ConfigurationPostProcessor configurationPostProcessor : configurationPostProcessors) {
            value = configurationPostProcessor.postProcessConfiguration(key, value);
        }
        return value;
    }
}

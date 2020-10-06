package com.inomera.telco.commons.config.service;

import com.inomera.telco.commons.config.fault.ConfigurationManagerRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Serdar Kuzucu
 */
class PropertyResourceConfigurationFetcherServiceTest {

    @Test
    @DisplayName("Configuration from second resource overrides the ones from the first resource")
    void shouldLoadOverrideDefaultPropertiesWithExternalProperties() {
        final PropertyResourceConfigurationFetcherService configurationFetcherService =
                new PropertyResourceConfigurationFetcherService(true, () -> fromString("a=1\nb=2"), () -> fromString("b=3\nc=4"));
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(3, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("3", allConfigurations.get("b"));
        assertEquals("4", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should not throw exception when null resource provided")
    void shouldNotThrowExceptionWhenAResourceIsNull() {
        final PropertyResourceConfigurationFetcherService configurationFetcherService =
                new PropertyResourceConfigurationFetcherService(false, () -> fromString("a=1\nb=2"), null);
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(2, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
    }

    @Test
    @DisplayName("Should not throw exception when a resource does not exist")
    void shouldNotThrowExceptionWhenAResourceNotExists() {
        final PropertyResourceConfigurationFetcherService configurationFetcherService =
                new PropertyResourceConfigurationFetcherService(false, () -> {
                    throw new RuntimeException("Resource does not exist");
                }, () -> fromString("b=3\nc=4"));
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(2, allConfigurations.size());
        assertEquals("3", allConfigurations.get("b"));
        assertEquals("4", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should throw exception when a resource cannot be read and fail-fast enabled")
    void shouldThrowExceptionWhenAResourceCanNotBeReadAndFailFastEnabled() {
        final PropertyResourceConfigurationFetcherService configurationFetcherService =
                new PropertyResourceConfigurationFetcherService(true, () -> fromString("a=1\nb=2"), () -> {
                    throw new RuntimeException("Cannot read!");
                });

        assertThrows(ConfigurationManagerRuntimeException.class, configurationFetcherService::fetchConfiguration);
    }

    private InputStream fromString(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}

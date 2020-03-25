package com.inomera.telco.commons.config.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Serdar Kuzucu
 */
class PropertyResourceConfigurationFetcherServiceTest {

    @Test
    @DisplayName("Configuration from second resource overrides the ones from the first resource")
    void shouldLoadOverrideDefaultPropertiesWithExternalProperties() throws IOException {
        final Resource resource1 = mock(Resource.class);
        when(resource1.getInputStream()).thenReturn(fromString("a=1\nb=2"));
        when(resource1.exists()).thenReturn(true);

        final Resource resource2 = mock(Resource.class);
        when(resource2.getInputStream()).thenReturn(fromString("b=3\nc=4"));
        when(resource2.exists()).thenReturn(true);

        final PropertyResourceConfigurationFetcherService configurationFetcherService = new PropertyResourceConfigurationFetcherService(true, resource1, resource2);
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(3, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("3", allConfigurations.get("b"));
        assertEquals("4", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should not throw exception when null resource provided")
    void shouldNotThrowExceptionWhenAResourceIsNull() throws IOException {
        final Resource resource1 = mock(Resource.class);
        when(resource1.getInputStream()).thenReturn(fromString("a=1\nb=2"));
        when(resource1.exists()).thenReturn(true);

        final PropertyResourceConfigurationFetcherService configurationFetcherService = new PropertyResourceConfigurationFetcherService(true, resource1, null);
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(2, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
    }

    @Test
    @DisplayName("Should not throw exception when a resource does not exist")
    void shouldNotThrowExceptionWhenAResourceNotExists() throws IOException {
        final Resource resource1 = mock(Resource.class);
        when(resource1.getInputStream()).thenReturn(null);
        when(resource1.exists()).thenReturn(false);

        final Resource resource2 = mock(Resource.class);
        when(resource2.getInputStream()).thenReturn(fromString("b=3\nc=4"));
        when(resource2.exists()).thenReturn(true);

        final PropertyResourceConfigurationFetcherService configurationFetcherService = new PropertyResourceConfigurationFetcherService(true, resource1, resource2);
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(2, allConfigurations.size());
        assertEquals("3", allConfigurations.get("b"));
        assertEquals("4", allConfigurations.get("c"));
    }

    @Test
    @DisplayName("Should throw exception when a resource cannot be read and fail-fast enabled")
    void shouldThrowExceptionWhenAResourceCanNotBeReadAndFailFastEnabled() throws IOException {
        final Resource resource1 = mock(Resource.class);
        when(resource1.getInputStream()).thenReturn(fromString("a=1\nb=2"));
        when(resource1.exists()).thenReturn(true);

        final Resource resource2 = mock(Resource.class);
        when(resource2.getInputStream()).thenThrow(new RuntimeException("Cannot read!"));
        when(resource2.exists()).thenReturn(true);

        final PropertyResourceConfigurationFetcherService configurationFetcherService = new PropertyResourceConfigurationFetcherService(true, resource1, resource2);

        assertThrows(PropertyFileReadException.class, configurationFetcherService::fetchConfiguration);
    }

    @Test
    @DisplayName("Should not throw exception when a resource cannot be read and fail-fast disabled")
    void shouldNotThrowExceptionWhenAResourceCanNotBeReadAndFailFastDisabled() throws IOException {
        final Resource resource1 = mock(Resource.class);
        when(resource1.getInputStream()).thenReturn(fromString("a=1\nb=2"));
        when(resource1.exists()).thenReturn(true);

        final Resource resource2 = mock(Resource.class);
        when(resource2.getInputStream()).thenThrow(new RuntimeException("Cannot read!"));
        when(resource2.exists()).thenReturn(true);

        final PropertyResourceConfigurationFetcherService configurationFetcherService = new PropertyResourceConfigurationFetcherService(false, resource1, resource2);
        final Map<String, String> allConfigurations = configurationFetcherService.fetchConfiguration();

        assertEquals(2, allConfigurations.size());
        assertEquals("1", allConfigurations.get("a"));
        assertEquals("2", allConfigurations.get("b"));
    }

    private InputStream fromString(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}

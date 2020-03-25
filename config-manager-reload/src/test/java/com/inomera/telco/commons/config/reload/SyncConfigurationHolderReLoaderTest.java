package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class SyncConfigurationHolderReLoaderTest {

    @InjectMocks
    private SyncConfigurationHolderReLoader reLoader;

    @Mock
    private ConfigurationHolder configurationHolder;

    @Test
    @DisplayName("Should call configuration holder directly and synchronously")
    void shouldCallConfigurationHolderDirectly() {
        reLoader.reloadConfigurations();
        verify(configurationHolder, times(1)).reloadConfigurations();
    }

    @Test
    @DisplayName("Should not catch any exceptions")
    void shouldNotCatchAnyExceptions() {
        doThrow(new RuntimeException("ABC")).when(configurationHolder).reloadConfigurations();

        assertThrows(RuntimeException.class, reLoader::reloadConfigurations);
    }
}

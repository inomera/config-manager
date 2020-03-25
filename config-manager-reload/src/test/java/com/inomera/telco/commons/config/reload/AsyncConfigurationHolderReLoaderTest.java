package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class AsyncConfigurationHolderReLoaderTest {

    @Mock
    private ConfigurationHolderReLoader delegate;

    @Mock
    private Executor executor;

    @Mock
    private ConfigurationHolder configurationHolder;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Test
    @DisplayName("Should call delegate when reloadConfigurations called")
    void shouldCallDelegateOverExecutorWhenReloadConfigurationsCalled() {
        final AsyncConfigurationHolderReLoader reLoader = new AsyncConfigurationHolderReLoader(delegate, executor);

        reLoader.reloadConfigurations();

        verify(executor, times(1)).execute(runnableArgumentCaptor.capture());
        verify(delegate, never()).reloadConfigurations();
        final Runnable runnable = runnableArgumentCaptor.getValue();

        runnable.run();

        verify(delegate, times(1)).reloadConfigurations();
    }

    @Test
    @DisplayName("Should call ConfigurationHolder when delegate is not provided")
    void shouldCallConfigurationHolderWhenDelegateIsNotProvided() {
        final AsyncConfigurationHolderReLoader reLoader = new AsyncConfigurationHolderReLoader(configurationHolder, executor);

        reLoader.reloadConfigurations();

        verify(executor, times(1)).execute(runnableArgumentCaptor.capture());
        verify(configurationHolder, never()).reloadConfigurations();
        final Runnable runnable = runnableArgumentCaptor.getValue();

        runnable.run();

        verify(configurationHolder, times(1)).reloadConfigurations();
    }

    @Test
    @DisplayName("Should catch exceptions in reloadConfigurations")
    void shouldCatchExceptionsInReloadConfigurations() {
        final AsyncConfigurationHolderReLoader reLoader = new AsyncConfigurationHolderReLoader(configurationHolder, executor);
        doThrow(new RuntimeException("DEF")).when(configurationHolder).reloadConfigurations();

        reLoader.reloadConfigurations();

        verify(executor, times(1)).execute(runnableArgumentCaptor.capture());
        verify(configurationHolder, never()).reloadConfigurations();
        final Runnable runnable = runnableArgumentCaptor.getValue();

        assertDoesNotThrow(runnable::run);

        verify(configurationHolder, times(1)).reloadConfigurations();
    }
}

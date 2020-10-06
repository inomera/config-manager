package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.lang.scheduling.Trigger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class ScheduledConfigurationHolderReLoaderTest {
    @Mock
    private ConfigurationHolderReLoader delegate;

    @Mock
    private ScheduledExecutorService executorService;

    @Mock
    private Trigger trigger;

    @Mock
    private ConfigurationHolder configurationHolder;

    @Mock
    private ScheduledFuture<Object> future;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Test
    @DisplayName("Constructor with delegate should schedule task with the given trigger")
    void delegatedConstructorShouldScheduleTaskWithGivenTrigger() {
        doReturn(new Date(System.currentTimeMillis() + 1000L * 60L)).when(trigger).nextExecutionTime(any());
        new ScheduledConfigurationHolderReLoader(delegate, executorService, trigger);
        verify(executorService, times(1)).schedule(runnableArgumentCaptor.capture(), leq(1000L * 60L), eq(TimeUnit.MILLISECONDS));
        assertNotNull(runnableArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("Constructor with ConfigurationHolder should schedule task with the given trigger")
    void constructorWithConfigurationHolderShouldScheduleTaskWithGivenTrigger() {
        doReturn(new Date(System.currentTimeMillis() + 1000L * 60L)).when(trigger).nextExecutionTime(any());
        new ScheduledConfigurationHolderReLoader(configurationHolder, executorService, trigger);
        verify(executorService, times(1)).schedule(runnableArgumentCaptor.capture(), leq(1000L * 60L), eq(TimeUnit.MILLISECONDS));
        assertNotNull(runnableArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("Triggered runnable should reload configurations")
    void triggeredRunnableShouldReloadConfigurations() {
        doReturn(new Date(System.currentTimeMillis() + 1000L * 60L)).when(trigger).nextExecutionTime(any());
        doReturn(future).when(executorService).schedule(any(Runnable.class), anyLong(), any());

        new ScheduledConfigurationHolderReLoader(configurationHolder, executorService, trigger);
        verify(executorService, times(1)).schedule(runnableArgumentCaptor.capture(), leq(1000L * 60L), eq(TimeUnit.MILLISECONDS));
        verify(configurationHolder, never()).reloadConfigurations();

        final Runnable runnable = runnableArgumentCaptor.getValue();
        assertNotNull(runnable);

        runnable.run();

        verify(configurationHolder, times(1)).reloadConfigurations();
    }

    @Test
    @DisplayName("Should not catch exceptions when triggered manually")
    void shouldThrowExceptionsWhileExecuting() {
        doReturn(new Date(System.currentTimeMillis() + 1000L * 60L)).when(trigger).nextExecutionTime(any());
        final ScheduledConfigurationHolderReLoader reLoader = new ScheduledConfigurationHolderReLoader(configurationHolder, executorService, trigger);
        verify(executorService, times(1)).schedule(runnableArgumentCaptor.capture(), leq(1000L * 60L), eq(TimeUnit.MILLISECONDS));
        verify(configurationHolder, never()).reloadConfigurations();

        doThrow(new RuntimeException("WWW")).when(configurationHolder).reloadConfigurations();

        assertThrows(RuntimeException.class, reLoader::reloadConfigurations);

        verify(configurationHolder, times(1)).reloadConfigurations();
    }
}

package com.inomera.telco.commons.config.reload;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import com.inomera.telco.commons.config.ConfigurationHolder;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class ScheduledConfigurationHolderReLoaderTest {

	@Mock
	private ConfigurationHolderReLoader delegate;

	@Mock
	private TaskScheduler taskScheduler;

	@Mock
	private Trigger trigger;

	@Mock
	private ConfigurationHolder configurationHolder;

	@Captor
	private ArgumentCaptor<Runnable> runnableArgumentCaptor;

	@Test
	@DisplayName("Constructor with delegate should schedule task with the given trigger")
	void delegatedConstructorShouldScheduleTaskWithGivenTrigger() {
		new ScheduledConfigurationHolderReLoader(delegate, taskScheduler, trigger);
		verify(taskScheduler, times(1)).schedule(runnableArgumentCaptor.capture(), same(trigger));
		assertNotNull(runnableArgumentCaptor.getValue());
	}

	@Test
	@DisplayName("Constructor with ConfigurationHolder should schedule task with the given trigger")
	void constructorWithConfigurationHolderShouldScheduleTaskWithGivenTrigger() {
		new ScheduledConfigurationHolderReLoader(configurationHolder, taskScheduler, trigger);
		verify(taskScheduler, times(1)).schedule(runnableArgumentCaptor.capture(), same(trigger));
		assertNotNull(runnableArgumentCaptor.getValue());
	}

	@Test
	@DisplayName("Triggered runnable should reload configurations")
	void triggeredRunnableShouldReloadConfigurations() {
		new ScheduledConfigurationHolderReLoader(configurationHolder, taskScheduler, trigger);
		verify(taskScheduler, times(1)).schedule(runnableArgumentCaptor.capture(), same(trigger));
		verify(configurationHolder, never()).reloadConfigurations();

		final Runnable runnable = runnableArgumentCaptor.getValue();
		assertNotNull(runnable);

		runnable.run();

		verify(configurationHolder, times(1)).reloadConfigurations();
	}

	@Test
	@DisplayName("Should catch exceptions while executing scheduled")
	void shouldCatchExceptionsWhileExecutingScheduled() {
		new ScheduledConfigurationHolderReLoader(configurationHolder, taskScheduler, trigger);
		verify(taskScheduler, times(1)).schedule(runnableArgumentCaptor.capture(), same(trigger));
		verify(configurationHolder, never()).reloadConfigurations();

		doThrow(new RuntimeException("WWW")).when(configurationHolder).reloadConfigurations();

		final Runnable runnable = runnableArgumentCaptor.getValue();
		assertNotNull(runnable);

		assertDoesNotThrow(runnable::run);

		verify(configurationHolder, times(1)).reloadConfigurations();
	}

	@Test
	@DisplayName("Should not catch exceptions when triggered manually")
	void shouldThrowExceptionsWhileExecutingNonScheduled() {
		final ScheduledConfigurationHolderReLoader reLoader = new ScheduledConfigurationHolderReLoader(configurationHolder, taskScheduler, trigger);
		verify(taskScheduler, times(1)).schedule(any(), same(trigger));
		verify(configurationHolder, never()).reloadConfigurations();

		doThrow(new RuntimeException("WWW")).when(configurationHolder).reloadConfigurations();

		assertThrows(RuntimeException.class, reLoader::reloadConfigurations);

		verify(configurationHolder, times(1)).reloadConfigurations();
	}
}
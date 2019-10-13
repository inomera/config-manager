package com.inomera.telco.commons.config.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import com.inomera.telco.commons.config.ConfigurationHolder;

/**
 * Schedules re-loading with spring's TaskScheduler and Trigger classes.
 *
 * @author Serdar Kuzucu
 */
public class ScheduledConfigurationHolderReLoader implements ConfigurationHolderReLoader {
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledConfigurationHolderReLoader.class);

	private final ConfigurationHolderReLoader delegate;

	public ScheduledConfigurationHolderReLoader(ConfigurationHolderReLoader delegate, TaskScheduler taskScheduler, Trigger trigger) {
		this.delegate = delegate;
		taskScheduler.schedule(this::reloadConfigurationsScheduled, trigger);
	}

	public ScheduledConfigurationHolderReLoader(ConfigurationHolder configurationHolder, TaskScheduler taskScheduler, Trigger trigger) {
		this(new SyncConfigurationHolderReLoader(configurationHolder), taskScheduler, trigger);
	}

	@Override
	public void reloadConfigurations() {
		LOG.info("reloadConfigurations started");

		delegate.reloadConfigurations();

		LOG.info("reloadConfigurations finished");
	}

	private void reloadConfigurationsScheduled() {
		try {
			this.reloadConfigurations();
		} catch (Exception e) {
			LOG.error("Error in reloadConfigurationsScheduled: {}", e.getMessage(), e);
		}
	}
}

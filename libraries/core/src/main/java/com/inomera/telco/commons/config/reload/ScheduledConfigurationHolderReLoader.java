package com.inomera.telco.commons.config.reload;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.lang.scheduling.CronTrigger;
import com.inomera.telco.commons.lang.scheduling.PeriodicTrigger;
import com.inomera.telco.commons.lang.scheduling.ReschedulingRunnable;
import com.inomera.telco.commons.lang.scheduling.Trigger;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules re-loading with ScheduledExecutorService and Trigger classes.
 *
 * @author Serdar Kuzucu
 */
@Slf4j
public class ScheduledConfigurationHolderReLoader implements ConfigurationHolderReLoader {
    private final ConfigurationHolderReLoader delegate;

    public ScheduledConfigurationHolderReLoader(
            ConfigurationHolderReLoader delegate, ScheduledExecutorService executor, String cronExpression) {
        this(delegate, executor, new CronTrigger(cronExpression));
    }

    public ScheduledConfigurationHolderReLoader(
            ConfigurationHolder configurationHolder, ScheduledExecutorService executor, String cronExpression) {
        this(configurationHolder, executor, new CronTrigger(cronExpression));
    }

    public ScheduledConfigurationHolderReLoader(
            ConfigurationHolderReLoader delegate, ScheduledExecutorService executor,
            long reloadPeriod, TimeUnit reloadPeriodUnit) {
        this(delegate, executor, new PeriodicTrigger(reloadPeriod, reloadPeriodUnit));
    }

    public ScheduledConfigurationHolderReLoader(
            ConfigurationHolder configurationHolder, ScheduledExecutorService executor,
            long reloadPeriod, TimeUnit reloadPeriodUnit) {
        this(configurationHolder, executor, new PeriodicTrigger(reloadPeriod, reloadPeriodUnit));
    }

    public ScheduledConfigurationHolderReLoader(ConfigurationHolder configurationHolder, ScheduledExecutorService executor, Trigger trigger) {
        this(new SyncConfigurationHolderReLoader(configurationHolder), executor, trigger);
    }

    public ScheduledConfigurationHolderReLoader(ConfigurationHolderReLoader delegate, ScheduledExecutorService executor, Trigger trigger) {
        this.delegate = delegate;
        new ReschedulingRunnable(this::reloadConfigurations, trigger, executor, this::logError)
                .schedule();
    }

    @Override
    public void reloadConfigurations() {
        LOG.debug("reloadConfigurations started");
        delegate.reloadConfigurations();
        LOG.debug("reloadConfigurations finished");
    }

    private void logError(Throwable throwable) {
        LOG.error("Error in reloadConfigurationsScheduled: {}", throwable.getMessage(), throwable);
    }
}

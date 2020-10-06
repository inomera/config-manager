package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.reload.ConfigurationHolderReLoader;
import com.inomera.telco.commons.config.reload.ScheduledConfigurationHolderReLoader;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerReloadEnabledCondition;
import com.inomera.telco.commons.lang.scheduling.CronTrigger;
import com.inomera.telco.commons.lang.scheduling.PeriodicTrigger;
import com.inomera.telco.commons.lang.scheduling.Trigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.inomera.telco.commons.config.spring.BeanNames.*;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@Conditional(ConfigManagerReloadEnabledCondition.class)
public class ConfigurationReloadAutoConfiguration {

    public ConfigurationReloadAutoConfiguration() {
        System.out.println();
    }

    @Bean(BEAN_CM_RELOAD_SCHEDULER)
    @ConditionalOnMissingBean(name = BEAN_CM_RELOAD_SCHEDULER)
    public ScheduledExecutorService configurationHolderReLoaderTaskScheduler() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Bean(BEAN_CM_RE_LOADER)
    @ConditionalOnMissingBean(name = BEAN_CM_RE_LOADER)
    public ConfigurationHolderReLoader configurationHolderReLoader(
            @Qualifier(BEAN_CONFIGURATION_HOLDER) ConfigurationHolder configurationHolder,
            @Qualifier(BEAN_CM_RELOAD_SCHEDULER) ScheduledExecutorService scheduler,
            @Qualifier(BEAN_CM_RELOAD_TRIGGER) Trigger trigger) {
        return new ScheduledConfigurationHolderReLoader(configurationHolder, scheduler, trigger);
    }

    @Bean(name = BEAN_CM_RELOAD_TRIGGER)
    @ConditionalOnMissingBean(name = BEAN_CM_RELOAD_TRIGGER)
    @ConditionalOnProperty(name = "config-manager.reload.trigger", havingValue = "cron")
    public Trigger configurationReloadCronTrigger(
            @Value("${config-manager.reload.cron-expression}") String cronExpression) {
        return new CronTrigger(cronExpression);
    }

    @Bean(name = BEAN_CM_RELOAD_TRIGGER)
    @ConditionalOnMissingBean(name = BEAN_CM_RELOAD_TRIGGER)
    @ConditionalOnProperty(name = "config-manager.reload.trigger", havingValue = "periodical")
    public Trigger configurationReloadTrigger(
            @Value("${config-manager.reload.period}") Long period,
            @Value("${config-manager.reload.period-unit}") TimeUnit periodUnit) {
        return new PeriodicTrigger(period, periodUnit);
    }
}

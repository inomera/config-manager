package com.inomera.telco.commons.springconfigmanagerstarter;

import com.inomera.telco.commons.config.ConcurrentHashMapConfigurationHolder;
import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.config.dao.NamedParameterJdbcTemplateConfigurationDaoImpl;
import com.inomera.telco.commons.config.reload.ConfigurationHolderReLoader;
import com.inomera.telco.commons.config.reload.ScheduledConfigurationHolderReLoader;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import com.inomera.telco.commons.config.service.DaoConfigurationService;
import com.inomera.telco.commons.config.service.PropertyResourceConfigurationFetcherService;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.reentrant.LocalReentrantLockProvider;
import com.inomera.telco.commons.springconfigmanagerstarter.ConfigManagerProperties.DataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@ConditionalOnClass(ConfigurationHolder.class)
@ConditionalOnProperty(value = "config-manager.enabled")
@EnableConfigurationProperties(value = ConfigManagerProperties.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ConfigManagerAutoConfiguration {

    @Autowired
    private ConfigManagerProperties configManagerProperties;

    @Bean
    @ConditionalOnMissingBean
    public LockProvider lockProvider() {
        return new LocalReentrantLockProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationHolder configurationHolder(@Qualifier("configurationFetcherService") ConfigurationFetcherService configurationFetcherService) {
        return new ConcurrentHashMapConfigurationHolder(configurationFetcherService);
    }

    @Bean(name = "configurationHolderReLoaderTaskScheduler")
    @ConditionalOnMissingBean(name = "configurationHolderReLoaderTaskScheduler")
    public TaskScheduler configurationHolderReLoaderTaskScheduler() {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        return threadPoolTaskScheduler;
    }

    @Bean
    @ConditionalOnProperty(name = "config-manager.auto-reload")
    public ConfigurationHolderReLoader configurationHolderReLoader(
            ConfigurationHolder configurationHolder,
            @Qualifier("configurationHolderReLoaderTaskScheduler") TaskScheduler scheduler,
            @Qualifier("configurationReloadTrigger") Trigger trigger
    ) {
        return new ScheduledConfigurationHolderReLoader(configurationHolder, scheduler, trigger);
    }

    @Bean(name = "configurationReloadTrigger")
    @ConditionalOnProperty(name = "config-manager.reload-trigger", havingValue = "cron")
    public Trigger configurationReloadCronTrigger() {
        return new CronTrigger(configManagerProperties.getReloadCronExpression());
    }

    @Bean(name = "configurationReloadTrigger")
    @ConditionalOnProperty(name = "config-manager.reload-trigger", havingValue = "periodical")
    public Trigger configurationReloadTrigger() {
        return new PeriodicTrigger(configManagerProperties.getReloadPeriodInMilliseconds());
    }

    @Configuration
    @ConditionalOnProperty(name = "config-manager.source", havingValue = "jdbc")
    public static class JdbcConfigurationSourceConfiguration {

        @Autowired
        private ConfigManagerProperties configManagerProperties;

        @Bean(name = "configManagerDataSource")
        @ConditionalOnMissingBean(name = "configManagerDataSource")
        public DataSource configManagerDataSource() {
            final DataSourceProperties dataSourceProperties = configManagerProperties.getDataSource();

            final DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(dataSourceProperties.getDriverClassName());
            dataSourceBuilder.url(dataSourceProperties.getUrl());
            dataSourceBuilder.username(dataSourceProperties.getUsername());
            dataSourceBuilder.password(dataSourceProperties.getPassword());
            return dataSourceBuilder.build();
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(value = DataSource.class, name = "configManagerDataSource")
        public ConfigurationDao configurationDao(@Qualifier("configManagerDataSource") DataSource dataSource) {
            final ConfigManagerProperties.SqlStatements sql = configManagerProperties.getSql();
            return new NamedParameterJdbcTemplateConfigurationDaoImpl(dataSource, sql.getSelect(), sql.getInsert(), sql.getUpdate(), sql.getDeleteByPrefix());
        }

        @Bean(name = "configManagerTransactionTemplate")
        @ConditionalOnBean(value = DataSource.class, name = "configManagerDataSource")
        @ConditionalOnMissingBean(name = "configManagerTransactionTemplate")
        public TransactionTemplate configManagerTransactionTemplate(@Qualifier("configManagerDataSource") DataSource dataSource) {
            final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
            return new TransactionTemplate(transactionManager);
        }

        @Bean
        @ConditionalOnMissingBean
        public ConfigurationFetcherService configurationFetcherService(
                @Qualifier("configManagerTransactionTemplate") TransactionTemplate transactionTemplate,
                LockProvider lockProvider,
                ConfigurationDao configurationDao
        ) {
            final int concatConfigurationMaxLength = configManagerProperties.getValueColumnLength();
            return new DaoConfigurationService(transactionTemplate, lockProvider, configurationDao, concatConfigurationMaxLength);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "config-manager.source", havingValue = "properties")
    public static class PropertiesConfigurationSourceConfiguration {

        @Autowired
        private ConfigManagerProperties configManagerProperties;

        @Bean
        @ConditionalOnMissingBean
        public ConfigurationFetcherService configurationFetcherService(ApplicationContext applicationContext) {
            return new PropertyResourceConfigurationFetcherService(false, getResources(applicationContext));
        }

        private Resource[] getResources(ApplicationContext applicationContext) {
            final List<String> propertyFiles = configManagerProperties.getPropertyFiles();
            return propertyFiles.stream().map(applicationContext::getResource).toArray(Resource[]::new);
        }
    }
}

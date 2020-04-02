package com.inomera.telco.commons.springconfigmanagerstarter;

import com.inomera.telco.commons.config.ConcurrentHashMapConfigurationHolder;
import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.dao.MongoConfigurationDao;
import com.inomera.telco.commons.config.dao.NoSqlConfigurationDao;
import com.inomera.telco.commons.config.reload.ConfigurationHolderReLoader;
import com.inomera.telco.commons.config.reload.ScheduledConfigurationHolderReLoader;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import com.inomera.telco.commons.config.service.NoSqlConfigurationFetcherService;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

/**
 * @author Melek UZUN
 */
@Configuration
@ConditionalOnClass(ConfigurationHolder.class)
@ConditionalOnProperty(value = "config-manager.enabled")
@EnableConfigurationProperties(value = ConfigManagerProperties.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ConfigManagerNoSqlAutoConfiguration {

    @Autowired
    private ConfigManagerProperties configManagerProperties;

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationHolder configurationHolder(
            @Qualifier("configurationFetcherService") ConfigurationFetcherService configurationFetcherService) {
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


    @Bean(name = "configManagerMongoClient")
    @ConditionalOnMissingBean(name = "configManagerMongoClient")
    public MongoClient mongoClient() {
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(mongoDBConfigurationProperties().getServerAddressList()))
                .applyToSslSettings(mongoDBConfigurationProperties().getSslSettings())
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    @ConfigurationProperties(prefix = "config-manager.mongo")
    public MongoDBConfigurationProperties mongoDBConfigurationProperties() {
        return new MongoDBConfigurationProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoConfigurationDao noSqlConfigurationDao(
            @Qualifier("configManagerMongoClient") MongoClient mongoClient
    ) {
        final MongoDBConfigurationProperties mongoDBConfigurationProperties = mongoDBConfigurationProperties();
        return new MongoConfigurationDao(mongoClient, mongoDBConfigurationProperties.getDbName(),
                mongoDBConfigurationProperties.getCollection(), mongoDBConfigurationProperties.getKeyFieldName(),
                mongoDBConfigurationProperties.getValueFieldName(), new Document(mongoDBConfigurationProperties.getQuery()));
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationFetcherService configurationFetcherService(
            NoSqlConfigurationDao noSqlConfigurationDao
    ) {
        return new NoSqlConfigurationFetcherService(noSqlConfigurationDao);
    }
}

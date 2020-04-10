package com.inomera.telco.commons.springconfigmanagerstarter;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.inomera.telco.commons.config.ConcurrentHashMapConfigurationHolder;
import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.dao.CassandraConfigurationDao;
import com.inomera.telco.commons.config.dao.CassandraConfigurationDaoImpl;
import com.inomera.telco.commons.config.reload.ConfigurationHolderReLoader;
import com.inomera.telco.commons.config.reload.ScheduledConfigurationHolderReLoader;
import com.inomera.telco.commons.config.service.CassandraConfigurationFetcherService;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Melek UZUN
 */
@Configuration
@ConditionalOnClass(ConfigurationHolder.class)
@ConditionalOnProperty(value = "config-manager.enabled")
@EnableConfigurationProperties(value = ConfigManagerProperties.class)
public class ConfigManagerCassandraAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigManagerCassandraAutoConfiguration.class);

    @Autowired
    private ConfigManagerProperties configManagerProperties;

    @Bean()
    @ConditionalOnMissingBean()
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

    @Bean
    @ConditionalOnMissingBean
    public CassandraConfigurationDao configurationDao(
            @Qualifier("cassandraSession") Session session
    ) {
        final CassandraConfigurationProperties.SqlStatements sql = cassandraConfigurationProperties().getSql();
        return new CassandraConfigurationDaoImpl(session, sql.getSelect());
    }


    @Bean
    @ConditionalOnMissingBean
    public ConfigurationFetcherService configurationFetcherService(
            CassandraConfigurationDao cassandraConfigurationDao
    ) {
        return new CassandraConfigurationFetcherService(cassandraConfigurationDao);
    }

    @Bean
    @ConfigurationProperties(prefix = "config-manager.cassandra")
    public CassandraConfigurationProperties cassandraConfigurationProperties() {
        return new CassandraConfigurationProperties();
    }

    @Bean
    public Cluster cassandraCluster(CodecRegistry codecRegistry) {
        final Collection<InetSocketAddress> cassandraContactPoints = getCassandraContactPoints();

        return Cluster.builder()
                .withoutMetrics()
                .withoutJMXReporting()
                .addContactPointsWithPorts(cassandraContactPoints)
                .withCodecRegistry(codecRegistry)
                .build();
    }

    @Bean
    public CodecRegistry codecRegistry() {
        return CodecRegistry.DEFAULT_INSTANCE;
    }

    @Bean("cassandraSession")
    @ConditionalOnMissingBean(name = "cassandraSession")
    public Session cassandraSession(Cluster cluster) {
        return cluster.connect();
    }

    @Bean
    public MappingManager mappingManager(Session session) {
        return new MappingManager(session);
    }

    private Collection<InetSocketAddress> getCassandraContactPoints() {
        if (cassandraConfigurationProperties().getNodes() == null) {
            throw new IllegalArgumentException("Please set environment property to connect to cassandra.");
        }

        final List<InetSocketAddress> cassandraNodes = toInetSocketAddresses(cassandraConfigurationProperties().getNodes());
        LOG.info("getCassandraContactPoints::{}", cassandraNodes);
        return cassandraNodes;
    }

    private static List<InetSocketAddress> toInetSocketAddresses(Set<CassandraConfigurationProperties.CassandraNode> cassandraNodes) {
        List<InetSocketAddress> contactAddresses = newArrayList();
        cassandraNodes.forEach(cassandraNode -> {
            String host = cassandraNode.getHost();
            if (StringUtils.isBlank(host)) {
                throw new IllegalArgumentException("Please set environment host property to connect to cassandra.");
            }
            Integer port = cassandraNode.getPort();
            if (port == null) {
                throw new IllegalArgumentException("Please set environment port property to connect to cassandra.");
            }
            contactAddresses.add(new InetSocketAddress(host, port));
        });
        return contactAddresses;
    }
}

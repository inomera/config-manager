package com.inomera.telco.commons.config.spring.configuration;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SyncCqlSession;
import com.inomera.telco.commons.config.dao.CassandraConfigurationDaoImpl;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEnabledAndCassandraSourceCondition;
import com.inomera.telco.commons.config.spring.configurationproperties.CassandraConfigurationProperties;
import com.inomera.telco.commons.config.spring.configurationproperties.CassandraConfigurationProperties.CassandraNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.inomera.telco.commons.config.spring.BeanNames.*;

/**
 * @author Melek UZUN
 * @author Turgay CAN
 */
@Slf4j
@Configuration
@Conditional(ConfigManagerEnabledAndCassandraSourceCondition.class)
public class ConfigManagerCassandraAutoConfiguration {

    @Bean(name = BEAN_CM_CASSANDRA_CONFIG_PROPERTIES)
    @ConfigurationProperties(prefix = "config-manager.cassandra")
    public CassandraConfigurationProperties cassandraConfigurationProperties() {
        return new CassandraConfigurationProperties();
    }

    @Bean(name = BEAN_CM_CONFIGURATION_DAO)
    @ConditionalOnMissingBean(name = BEAN_CM_CONFIGURATION_DAO)
    public CassandraConfigurationDaoImpl configurationDao(
            @Qualifier(BEAN_CM_CASSANDRA_SESSION) SyncCqlSession session,
            CassandraConfigurationProperties cassandraConfigurationProperties) {
        return new CassandraConfigurationDaoImpl(session, cassandraConfigurationProperties.getSelectSql());
    }

    @Bean(value = BEAN_CM_CASSANDRA_SESSION, destroyMethod = "close")
    @ConditionalOnMissingBean(name = BEAN_CM_CASSANDRA_SESSION)
    public SyncCqlSession cassandraSession(@Qualifier(BEAN_CM_CASSANDRA_CONFIG_PROPERTIES)
                                           CassandraConfigurationProperties configurationProperties) {
        Collection<InetSocketAddress> cassandraContactPoints = getCassandraContactPoints(configurationProperties);
        final String username = configurationProperties.getUsername();
        final String password = configurationProperties.getPassword();
        return CqlSession.builder()
                .addContactPoints(cassandraContactPoints)
                .withAuthCredentials(username, password)
                .withLocalDatacenter(configurationProperties.getLocalDatacenter())
                .build();
    }

    private Collection<InetSocketAddress> getCassandraContactPoints(CassandraConfigurationProperties configurationProperties) {
        if (cassandraConfigurationProperties().getNodes() == null) {
            throw new IllegalArgumentException("Please set environment property to connect to cassandra.");
        }

        final List<InetSocketAddress> cassandraNodes = toInetSocketAddresses(configurationProperties.getNodes());
        LOG.info("getCassandraContactPoints::{}", cassandraNodes);
        return cassandraNodes;
    }

    private static List<InetSocketAddress> toInetSocketAddresses(Collection<CassandraNode> cassandraNodes) {
        final List<InetSocketAddress> contactAddresses = newArrayList();
        cassandraNodes.forEach(cassandraNode -> {
            final String host = cassandraNode.getHost();
            if (StringUtils.isBlank(host)) {
                throw new IllegalArgumentException("Please set environment host property to connect to cassandra.");
            }
            final Integer port = cassandraNode.getPort();
            if (port == null) {
                throw new IllegalArgumentException("Please set environment port property to connect to cassandra.");
            }
            contactAddresses.add(new InetSocketAddress(host, port));
        });
        return contactAddresses;
    }
}

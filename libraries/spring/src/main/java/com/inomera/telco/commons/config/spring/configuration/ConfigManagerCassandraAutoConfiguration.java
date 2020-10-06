package com.inomera.telco.commons.config.spring.configuration;

import com.datastax.driver.core.*;
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
            @Qualifier(BEAN_CM_CASSANDRA_SESSION) Session session,
            CassandraConfigurationProperties cassandraConfigurationProperties) {
        return new CassandraConfigurationDaoImpl(session, cassandraConfigurationProperties.getSelectSql());
    }

    @Bean(name = BEAN_CM_CASSANDRA_CLUSTER, destroyMethod = "close")
    @ConditionalOnMissingBean(name = BEAN_CM_CASSANDRA_CLUSTER)
    public Cluster cassandraCluster() {
        final Collection<InetSocketAddress> cassandraContactPoints = getCassandraContactPoints();

        return Cluster.builder()
                .withAuthProvider(cassandraAuthProvider(cassandraConfigurationProperties()))
                .withoutMetrics()
                .withoutJMXReporting()
                .addContactPointsWithPorts(cassandraContactPoints)
                .withCodecRegistry(CodecRegistry.DEFAULT_INSTANCE)
                .build();
    }

    @Bean(value = BEAN_CM_CASSANDRA_SESSION, destroyMethod = "close")
    @ConditionalOnMissingBean(name = BEAN_CM_CASSANDRA_SESSION)
    public Session cassandraSession(@Qualifier(BEAN_CM_CASSANDRA_CLUSTER) Cluster cluster) {
        return cluster.connect();
    }

    private AuthProvider cassandraAuthProvider(CassandraConfigurationProperties configurationProperties) {
        final String username = configurationProperties.getUsername();
        final String password = configurationProperties.getPassword();
        if (StringUtils.isAllBlank(username, password)) {
            return AuthProvider.NONE;
        }
        return new PlainTextAuthProvider(username, password);
    }

    private Collection<InetSocketAddress> getCassandraContactPoints() {
        if (cassandraConfigurationProperties().getNodes() == null) {
            throw new IllegalArgumentException("Please set environment property to connect to cassandra.");
        }

        final List<InetSocketAddress> cassandraNodes = toInetSocketAddresses(cassandraConfigurationProperties().getNodes());
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

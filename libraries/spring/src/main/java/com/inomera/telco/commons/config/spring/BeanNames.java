package com.inomera.telco.commons.config.spring;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Serdar Kuzucu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanNames {
    public static final String BEAN_CONFIGURATION_HOLDER = "config-manager.configuration-holder";
    public static final String BEAN_CM_RELOAD_TRIGGER = "config-manager.reload-trigger";
    public static final String BEAN_CM_RELOAD_SCHEDULER = "config-manager.reload-scheduler";
    public static final String BEAN_CM_RE_LOADER = "config-manager.re-loader";
    public static final String BEAN_CM_FETCHER_SERVICE = "config-manager.fetcher-service";
    public static final String BEAN_CM_CONFIGURATION_DAO = "config-manager.configuration-dao";
    public static final String BEAN_CM_JDBC_DATA_SOURCE = "config-manager.jdbc-data-source";
    public static final String BEAN_CM_MONGO_CLIENT = "config-manager.mongo-client";
    public static final String BEAN_CM_CASSANDRA_CLUSTER = "config-manager.cassandra-cluster";
    public static final String BEAN_CM_CASSANDRA_SESSION = "config-manager.cassandra-session";
    public static final String BEAN_CM_CASSANDRA_CONFIG_PROPERTIES = "config-manager.cassandra-configuration-properties";
    public static final String BEAN_CM_CONFIGURATION_DECRYPTOR = "config-manager.configuration-decryptor";
    public static final String BEAN_CM_JASYPT_ENCRYPTOR = "config-manager.jasypt-encryptor";
}

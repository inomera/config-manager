package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.dao.ConfigurationDao;
import com.inomera.telco.commons.config.dao.SqlDataSourceConfigurationDaoImpl;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEnabledAndJdbcSourceCondition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_CONFIGURATION_DAO;
import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_JDBC_DATA_SOURCE;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@Conditional(ConfigManagerEnabledAndJdbcSourceCondition.class)
public class ConfigManagerJdbcAutoConfiguration {

    @Bean(name = BEAN_CM_JDBC_DATA_SOURCE)
    @ConditionalOnMissingBean(name = BEAN_CM_JDBC_DATA_SOURCE)
    public DataSource configManagerDataSource(
            @Value("${config-manager.jdbc.driver-class-name}") String driverClassName,
            @Value("${config-manager.jdbc.url}") String url,
            @Value("${config-manager.jdbc.username}") String username,
            @Value("${config-manager.jdbc.password}") String password) {

        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = BEAN_CM_CONFIGURATION_DAO)
    @ConditionalOnMissingBean(name = BEAN_CM_CONFIGURATION_DAO)
    @ConditionalOnBean(name = BEAN_CM_JDBC_DATA_SOURCE)
    public ConfigurationDao configurationDao(
            @Qualifier(BEAN_CM_JDBC_DATA_SOURCE) DataSource dataSource,
            @Value("${config-manager.jdbc.select-sql}") String selectSql) {
        return new SqlDataSourceConfigurationDaoImpl(dataSource, selectSql);
    }
}

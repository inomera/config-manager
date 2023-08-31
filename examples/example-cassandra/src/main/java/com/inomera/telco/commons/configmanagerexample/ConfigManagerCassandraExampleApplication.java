package com.inomera.telco.commons.configmanagerexample;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.spring.configuration.ConfigurationHolderAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * @author Melek UZUN
 */
@SpringBootApplication
@Import(value = ConfigurationHolderAutoConfiguration.class)
public class ConfigManagerCassandraExampleApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigManagerCassandraExampleApplication.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(ConfigManagerCassandraExampleApplication.class, args);
        final ConfigurationHolder configurationHolder = context.getBean(ConfigurationHolder.class);

        final String demoValue = configurationHolder.getStringProperty("first-key");

        LOG.info("first-key: {}", demoValue);
    }
}

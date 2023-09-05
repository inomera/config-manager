package com.inomera.telco.commons.configmanagerexample;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.spring.configuration.ConfigurationHolderAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import(value = ConfigurationHolderAutoConfiguration.class)
public class ConfigManagerRedisExampleApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(ConfigManagerRedisExampleApplication.class, args);
        final ConfigurationHolder configurationHolder = context.getBean(ConfigurationHolder.class);

        final String demoValue = configurationHolder.getStringProperty("first-key");
        LOG.info("first-key: {}", demoValue);
    }
}

package com.inomera.telco.commons.configmanagerexample;

import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.lang.Assert;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication
public class ConfigManagerJdbcExampleApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigManagerJdbcExampleApplication.class);

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(ConfigManagerJdbcExampleApplication.class, args);
        final ConfigurationHolder configurationHolder = context.getBean(ConfigurationHolder.class);

        final String demoValue = configurationHolder.getStringProperty("first-key");
        final String decryptedValue = configurationHolder.getStringProperty("encrypted-key");

        LOG.info("first-key: {}", demoValue);
        assert demoValue != null;
        LOG.info("decryptedValue: {}", decryptedValue);
        assert decryptedValue != null;
    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @RequiredArgsConstructor
    public static class CreateTableConfiguration {
        private final JdbcTemplate jdbcTemplate;

        @PostConstruct
        public void createTable() {
            jdbcTemplate.execute("CREATE TABLE APP_SETTINGS ( " +
                    "                APPLICATION VARCHAR2(255 CHAR), " +
                    "                PROFILE     VARCHAR2(255 CHAR), " +
                    "                LABEL       VARCHAR2(255 CHAR), " +
                    "                \"KEY\"         VARCHAR2(255 CHAR), " +
                    "                \"VALUE\"       CLOB, " +
                    "                PRIMARY KEY (APPLICATION, PROFILE, LABEL, \"KEY\") " +
                    "            );");

            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('charging', 'default', 'master', 'first-key', 'first-value')");
            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('charging', 'default', 'master', 'second-key', 'second-value')");
            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('charging', 'default', 'master', 'third-key', 'third-value')");
            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('charging', 'default', 'master', 'fifth-key', 'fifth-value')");

            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('application', 'default', 'master', 'second-key', 'second-value-2')");
            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('application', 'default', 'master', 'first-key', 'first-value-2')");

            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('application', 'prod', 'master', 'second-key', 'second-value-3')");
            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('application', 'prod', 'master', 'fourth-key', 'fourth-value-3')");

            jdbcTemplate.execute("insert into app_settings (application, profile, label, \"KEY\", \"VALUE\") values ('charging', 'default', 'master', 'encrypted-key', '{cipher}IAzAD7/dHwZOtaCEKm/XKw==')");
        }
    }
}

package com.inomera.telco.commons.springconfigmanagerstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Melek UZUN
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "config-manager")
public class ConfigManagerProperties {
    private int reloadPeriodInMilliseconds;
    private String reloadCronExpression;
}

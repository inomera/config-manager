package com.inomera.telco.commons.springconfigmanagerstarter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Serdar Kuzucu
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "config-manager")
public class ConfigManagerProperties {
    private List<String> propertyFiles = new ArrayList<>();
    private int reloadPeriodInMilliseconds;
    private String reloadCronExpression;
}

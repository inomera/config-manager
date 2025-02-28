package com.inomera.telco.commons.config.spring.configurationproperties;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Melek UZUN
 * @author Turgay CAN
 */
@Getter
@Setter
public class CassandraConfigurationProperties {
    private List<CassandraNode> nodes = new ArrayList<>();
    private String username;
    private String password;
    private String selectSql;
    private String localDatacenter = "datacenter1";

    @Getter
    @Setter
    public static class CassandraNode {
        private String host;
        private Integer port;
    }
}

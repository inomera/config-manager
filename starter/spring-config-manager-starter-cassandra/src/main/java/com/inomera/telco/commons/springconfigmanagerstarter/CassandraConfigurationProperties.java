package com.inomera.telco.commons.springconfigmanagerstarter;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author Melek UZUN
 */
@Getter
@Setter
public class CassandraConfigurationProperties {
    private Set<CassandraNode> nodes = newHashSet();
    private String keyspace;
    private String schema;
    private SqlStatements sql = new SqlStatements();

    @Getter
    @Setter
    public static class CassandraNode {
        private String host;
        private Integer port;
    }

    @Getter
    @Setter
    public static class SqlStatements {
        private String select;
    }
}

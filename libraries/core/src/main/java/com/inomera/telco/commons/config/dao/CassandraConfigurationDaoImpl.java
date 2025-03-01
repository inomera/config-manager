package com.inomera.telco.commons.config.dao;


import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SyncCqlSession;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Melek UZUN
 * @author Turgay CAN
 */
@RequiredArgsConstructor
public class CassandraConfigurationDaoImpl implements ConfigurationDao {
    private final SyncCqlSession session;
    private final PreparedStatement selectByAppStatement;

    public CassandraConfigurationDaoImpl(SyncCqlSession session, String selectSql) {
        this.session = session;
        this.selectByAppStatement = session.prepare(selectSql);
    }

    @Override
    public Map<String, String> findAllConfigurations() {
        final BoundStatement boundStatement = selectByAppStatement.bind();
        final ResultSet resultSet = session.execute(boundStatement);
        return resultSet.all().stream()
                .collect(Collectors.toMap(row -> row.getString(0), row -> row.getString(1),
                (_, secondValue) -> secondValue));
    }

}

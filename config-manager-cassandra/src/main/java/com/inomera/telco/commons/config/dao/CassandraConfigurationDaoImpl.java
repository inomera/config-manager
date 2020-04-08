package com.inomera.telco.commons.config.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Melek UZUN
 */
@RequiredArgsConstructor
public class CassandraConfigurationDaoImpl implements CassandraConfigurationDao {

    private final String selectSql;

    private final Session session;

    private final String application;

    private PreparedStatement selectByAppStatement;


    public CassandraConfigurationDaoImpl(Session session, String selectSql, String application) {
        this.session = session;
        this.selectSql = selectSql;
        this.application = application;
        this.selectByAppStatement = session.prepare(selectSql);
    }

    @Override
    public Map<String, String> findAllConfigurations() {
        final BoundStatement boundStatement = selectByAppStatement.bind(application);
        final ResultSet resultSet = session.execute(boundStatement);
        return resultSet.all().stream().collect(Collectors.toMap(row -> row.getString(0), row -> row.getString(1),
                (firstValue, secondValue) -> secondValue));
    }

}

package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.dao.MongoConfigurationDao;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEnabledAndMongoSourceCondition;
import com.inomera.telco.commons.config.spring.configurationproperties.MongoDBConfigurationProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_CONFIGURATION_DAO;
import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_MONGO_CLIENT;

/**
 * @author Melek UZUN
 */
@Configuration
@Conditional(ConfigManagerEnabledAndMongoSourceCondition.class)
public class ConfigManagerMongoAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "config-manager.mongo")
    public MongoDBConfigurationProperties mongoDBConfigurationProperties() {
        return new MongoDBConfigurationProperties();
    }

    @Bean(name = BEAN_CM_MONGO_CLIENT)
    @ConditionalOnMissingBean(name = BEAN_CM_MONGO_CLIENT)
    public MongoClient mongoClient(MongoDBConfigurationProperties mongoDBConfigurationProperties) {
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(mongoDBConfigurationProperties.getServerAddressList()))
                .applyToSslSettings(mongoDBConfigurationProperties.getSslSettings())
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean(name = BEAN_CM_CONFIGURATION_DAO)
    @ConditionalOnMissingBean(name = BEAN_CM_CONFIGURATION_DAO)
    public MongoConfigurationDao configurationDao(
            @Qualifier(BEAN_CM_MONGO_CLIENT) MongoClient mongoClient,
            MongoDBConfigurationProperties mongoDBConfigurationProperties) {
        return new MongoConfigurationDao(mongoClient, mongoDBConfigurationProperties.getDbName(),
                mongoDBConfigurationProperties.getCollection(), mongoDBConfigurationProperties.getKeyFieldName(),
                mongoDBConfigurationProperties.getValueFieldName(), new Document(mongoDBConfigurationProperties.getQuery()));
    }
}

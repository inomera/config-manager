package com.inomera.telco.commons.springconfigmanagerstarter;

import com.inomera.telco.commons.config.ConcurrentHashMapConfigurationHolder;
import com.inomera.telco.commons.config.ConfigurationHolder;
import com.inomera.telco.commons.config.dao.MongoConfigurationDao;
import com.inomera.telco.commons.config.dao.NoSqlConfigurationDao;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import com.inomera.telco.commons.config.service.NoSqlConfigurationFetcherService;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;

/**
 * @author Melek UZUN
 */
@Configuration
@ConditionalOnClass(ConfigurationHolder.class)
@ConditionalOnProperty(value = "config-manager.enabled")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ConfigManagerNoSqlAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationHolder configurationHolder(@Qualifier("configurationFetcherService") ConfigurationFetcherService configurationFetcherService) {
        return new ConcurrentHashMapConfigurationHolder(configurationFetcherService);
    }

    private CodecRegistry getCodecRegistry() {
        final List<Codec<?>> codecList = new ArrayList<>();
        return CodecRegistries.fromRegistries(getDefaultCodecRegistry(), CodecRegistries.fromCodecs(codecList));
    }

    @Bean(name = "configManagerMongoDB")
    @ConditionalOnMissingBean(name = "configManagerMongoDB")
    public MongoClient mongoClient() {
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(mongoDBConfigurationProperties().getServerAddressList()))
                .applyToSslSettings(mongoDBConfigurationProperties().getSslSettings())
                .codecRegistry(getCodecRegistry())
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    @ConfigurationProperties(prefix = "config-manager.mongo")
    public MongoDBConfigurationProperties mongoDBConfigurationProperties() {
        return new MongoDBConfigurationProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoConfigurationDao noSqlConfigurationDao() {
        Map<String, Object> query = new HashMap<>();
        return new MongoConfigurationDao(mongoClient(), mongoDBConfigurationProperties().getDbName(),
                mongoDBConfigurationProperties().getCollection(), mongoDBConfigurationProperties().getKeyFieldName(),
                mongoDBConfigurationProperties().getValueFieldName(), new Document(query));
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationFetcherService configurationFetcherService(
            NoSqlConfigurationDao noSqlConfigurationDao
    ) {
        return new NoSqlConfigurationFetcherService(noSqlConfigurationDao);
    }
}

package com.inomera.telco.commons.springconfigmanagerstarter;

import com.mongodb.Block;
import com.mongodb.ServerAddress;
import com.mongodb.connection.SslSettings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Melek UZUN
 */
@Getter
@Setter
public class MongoDBConfigurationProperties {
    private List<MongoServerAddressConfigurationProperties> servers;
    private boolean isEnableSsl;
    private String dbName;
    private String collection;
    private String keyFieldName;
    private String valueFieldName;

    @Getter
    @Setter
    public static class MongoServerAddressConfigurationProperties {
        private String host;
        private int port;

        public ServerAddress toServerAddress() {
            return new ServerAddress(host, port);
        }
    }

    public List<ServerAddress> getServerAddressList() {
        return servers.stream().map(MongoServerAddressConfigurationProperties::toServerAddress).collect(Collectors.toList());
    }

    public Block<SslSettings.Builder> getSslSettings() {
        return (builder) -> SslSettings.builder().enabled(isEnableSsl()).build();
    }

}

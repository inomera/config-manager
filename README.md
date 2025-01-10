# Usage of This Library

## Usage with Properties Files

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
```
JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
```

```yaml
config-manager:
  enabled: true
  source: properties
  reload:
    enabled: true
    trigger: cron
    cron-expression: '0/2 * * * * ?'
  properties:
    property-files: classpath:settings.properties
```

## Usage with Relational DB

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
// Other dependencies for related jdbc driver
```
JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
// Other dependencies for related jdbc driver
```

```yaml
config-manager:
  enabled: true
  source: jdbc
  reload:
    enabled: true
    trigger: periodical
    period: 2
    period-unit: seconds
  jdbc:
    driver-class-name: org.h2.Driver
    url: 'jdbc:h2:mem:testdb;MODE=Oracle'
    username: sa
    password: password
    select-sql: SELECT KEY, VALUE FROM APP_SETTINGS
```

## Usage with Mongodb

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
compileOnly "org.mongodb:mongodb-driver-sync:4.8.1"
compileOnly "org.mongodb:mongodb-driver-core:4.8.1"
```
JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
implementation 'org.mongodb:mongodb-driver:3.11.2'
```

```yaml
config-manager:
  enabled: true
  source: mongo
  reload:
    enabled: true
    trigger: periodical
    period: 5
    period-unit: seconds
  mongo:
    servers:
      - host: 127.0.0.1
        port: 27017
    query:
      application: application
      label: master
      profile: default
    db-name: testDB
    value-field-name: value
    key-field-name: key
    collection: testCollection
```

## Usage with Cassandra

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
implementation 'com.datastax.cassandra:cassandra-driver-core:3.6.0'
implementation 'com.datastax.cassandra:cassandra-driver-mapping:3.6.0'
implementation 'com.datastax.cassandra:cassandra-driver-extras:3.6.0'
```
JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
implementation 'com.datastax.cassandra:cassandra-driver-core:3.6.0'
implementation 'com.datastax.cassandra:cassandra-driver-mapping:3.6.0'
implementation 'com.datastax.cassandra:cassandra-driver-extras:3.6.0'
```

```yaml
config-manager:
  enabled: true
  source: cassandra
  reload:
    enabled: true
    trigger: periodical
    period: 5
    period-unit: seconds
  cassandra:
    nodes:
      - host: localhost
        port: 9042
    username: cassandra
    password: cassandra
    select-sql: select key, value from example_cassandra.app_settings where application = 'application'
```

## Usage with Redis

To use redis as a data source, the following dependencies must be added.

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
implementation 'org.redisson:redisson:3.23.4'
```
JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
implementation 'org.redisson:redisson:3.23.4'
```

You need to specify **config-manager** related settings in your `application.yml` file.
The `config-manager.redis.redisson.map-key` value must be a hash name that holds a collection of application settings
stored in redis.

Below is an example of _singleServerConfig_. You can specify the Redisson
settings `config-manager.redis.redisson.config`
in YAML format. For more information
see https://github.com/redisson/redisson/wiki/2.-Configuration#262-single-instance-yaml-config-format.

```yaml
config-manager:
  enabled: true
  source: redis
  reload:
    enabled: true
    trigger: periodical
    period: 5
    period-unit: seconds
  redis:
    redisson:
      map-key: example-redis-dev
      config: |
        singleServerConfig:
          idleConnectionTimeout: 10000
          connectTimeout: 10000
          timeout: 3000
          retryAttempts: 3
          retryInterval: 1500
          password: null
          username: null
          subscriptionsPerConnection: 5
          clientName: null
          address: "redis://127.0.0.1:6379"
          subscriptionConnectionMinimumIdleSize: 1
          subscriptionConnectionPoolSize: 50
          connectionMinimumIdleSize: 10
          connectionPoolSize: 64
          database: 0
          dnsMonitoringInterval: 5000
        threads: 2
        nettyThreads: 4
        codec: !<org.redisson.codec.Kryo5Codec> {}
        transportMode: "NIO"
```

## Encryption

JDK 17
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:4.0.0'
implementation 'org.jasypt:jasypt:1.9.3'
```

JDK 8+
```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.4.0'
implementation 'org.jasypt:jasypt:1.9.3'
```

```yaml
config-manager:
  enabled: true
  source: properties
  reload:
    enabled: true
    trigger: cron
    cron-expression: '0/2 * * * * ?'
  properties:
    property-files: classpath:settings.properties
  encryption:
    enabled: true
    secret-key: '12345678901234567890123456789012'
```

# Publishing

To publish a version to maven repository,
you should edit your local gradle.properties file.

The file is: `/path-to-user-home/.gradle/gradle.properties`

For example: `~/.gradle/gradle.properties`

Add credentials for nexus repository to `gradle.properties` file.

Example `gradle.properties` file:

```
telcoTeamUsername=********
telcoTeamPassword=************************
```

Then execute `gradle` `publish` task on the project.

For example, to publish the project `config-manager-core`, `config-manager-spring`
you need to execute the following command in project root:

```
gradle :config-manager-spring:publish
``` 

```
gradle :config-manager-core:publish
``` 

The repository will not allow you to publish the same version twice.
You need to change version of the artifact every time you want to publish.

You can change version in `build.gradle` file of the sub-project.

```
build.gradle > publishing > publications > mavenJava > version
```

Please change the version wisely.

# Changelog

## 4.0.0 (2023-08-31)

- Upgrade Spring Boot 3.1.1 and JDK 17 ([@atakan](https://git.telco.inomera.com/atakan))

## 3.4.0 (2023-08-31)

- Add Redis support ([@fatihbozik](https://git.telco.inomera.com/fatihbozik))

## 3.3.0 (2021-06-11)

- Adds new converter methods for the following basic types ([@sedran](https://git.telco.inomera.com/sedran)) 
  - getDoubleProperty
  - getFloatProperty
  - getJsonSetProperty
  - getEnumValue
  - getEnumList
  - getEnumSet

## 3.1.0 (2021-06-11)

Add configuration post processor support and encryption ([@sedran](https://git.telco.inomera.com/sedran))

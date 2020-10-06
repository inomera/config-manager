# Usage of This Library

## Usage with Properties Files

```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.0.0'
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

```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.0.0'
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

```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.0.0'
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

```groovy
implementation 'com.inomera.telco.commons:config-manager-spring:3.0.0'
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

For example, to publish the project `lock-provider`, 
you need to execute the following command in project root:

```
gradle :lock-provider:publish
``` 

The repository will not allow you to publish the same version twice.
You need to change version of the artifact every time you want to publish.

You can change version in `build.gradle` file of the sub-project.

```
build.gradle > publishing > publications > mavenJava > version
```

Please change the version wisely.

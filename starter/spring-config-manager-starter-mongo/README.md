#### MONGODB As Configuration Source

```yaml
config-manager:
  enabled: true
  mongo:
    servers:
      - host: 127.0.0.1
        port: 27017
    query:
      application: application
      label: master
      profile: default
    keyFieldName: key
    valueFieldName: value
    dbName: testDB
    collection: testCollection
```
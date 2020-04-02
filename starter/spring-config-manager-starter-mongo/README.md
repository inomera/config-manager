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

#### Auto-Reload Periodically

```yaml
config-manager:
  enabled: true
  auto-reload: true
  reload-trigger: periodical
  reload-period-in-milliseconds: 60000
```

#### Auto-Reload with Cron Expression

```yaml
config-manager:
  enabled: true
  auto-reload: true
  reload-trigger: cron
  reload-cron-expression: 0/10 * * * * ?
```

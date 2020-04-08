#### Cassandra As Configuration Source

```yaml
config-manager:
  enabled: true
  auto-reload: true
  reload-trigger: periodical
  reload-period-in-milliseconds: 60000

cassandra:
  transaction:
    nodes:
      - host: localhost
        port: 9042
    keyspace: mps
    schema: create_if_not_exists
    sql:
      select: select key, value from mps.app_settings where application = ?
```

#### Auto-Reload Periodically

```yaml
config-manager:
  enabled: true
  # ... Other configurations above
  auto-reload: true
  reload-trigger: periodical
  reload-period-in-milliseconds: 60000
```

#### Auto-Reload with Cron Expression

```yaml
config-manager:
  enabled: true
  # ... Other configurations above
  auto-reload: true
  reload-trigger: cron
  reload-cron-expression: 0/10 * * * * ?
```

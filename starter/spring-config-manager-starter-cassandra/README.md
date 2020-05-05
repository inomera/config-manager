#### Cassandra As Configuration Source

```yaml
config-manager:
  enabled: true
  auto-reload: true
  reload-trigger: periodical
  reload-period-in-milliseconds: 60000
  cassandra:
    nodes:
      - host: localhost
        port: 9042
    # Username & password are optional
    username: cassandra
    password: cassandra
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

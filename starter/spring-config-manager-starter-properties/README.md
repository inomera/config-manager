#### Property Files as Configuration Sources

```yaml
config-manager:
  enabled: true
  property-files: classpath:settings.properties,file:///tmp/settings.properties
```

#### Auto-Reload Periodically

```yaml
config-manager:
  enabled: true
  property-files: classpath:settings.properties,file:///tmp/settings.properties
  auto-reload: true
  reload-trigger: periodical
  reload-period-in-milliseconds: 60000
```

#### Auto-Reload with Cron Expression

```yaml
config-manager:
  enabled: true
  property-files: classpath:settings.properties,file:///tmp/settings.properties
  auto-reload: true
  reload-trigger: cron
  reload-cron-expression: 0/10 * * * * ?
```

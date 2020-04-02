#### JDBC As Configuration Source

```yaml
config-manager:
  enabled: true
  value-column-length: 255
  datasource:
    driver-class-name: oracle.jdbc.driver.OracleDriver
    url: jdbc:oracle:thin:@127.0.0.1:1521:XE
    username: SA
    password: as
    hikari:
      poolName: config-manager-hikari-pool
      maximum-pool-size: 1
      minimum-idle: 1
  sql:
    select: SELECT SETTING_NAME, SETTING_VALUE FROM APP_SETTINGS
    update: UPDATE APP_SETTINGS SET SETTING_VALUE = ? WHERE SETTING_NAME = ?
    insert: INSERT INTO APP_SETTINGS (ID, SETTING_KEY, SETTING_VALUE) VALUES (SEQ_APP_SETTINGS_ID.nextVal, ?, ?)
    delete-by-prefix: DELETE FROM APP_SETTINGS WHERE SETTING_KEY LIKE CONCAT(:prefix, '.%')
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

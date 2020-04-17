CREATE KEYSPACE IF NOT EXISTS example_cassandra WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE example_cassandra.app_settings
(
    application text,
    key         text,
    value       text,
    PRIMARY KEY (application, key)
);


insert into example_cassandra.app_settings(application, key, value)
values ('application', 'first-key', 'first-value');
insert into example_cassandra.app_settings (application, key, value)
values ('application', 'second-key', 'second-value');
insert into example_cassandra.app_settings (application, key, value)
values ('application', 'third-key', 'third-value');
insert into example_cassandra.app_settings (application, key, value)
values ('application', 'fifth-key', 'fifth-value');

insert into example_cassandra.app_settings (application, key, value)
values ('charging', 'second-key', 'second-value-2');
insert into example_cassandra.app_settings (application, key, value)
values ('charging', 'first-key', 'first-value-2');

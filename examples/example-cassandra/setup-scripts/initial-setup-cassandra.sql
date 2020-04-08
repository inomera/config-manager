CREATE TABLE mps.app_settings(" +
                    "   application text," +
                    "   key text," +
                    "   value text," +
                    "   PRIMARY KEY (application, key));


insert into mps.app_settings(application,key,value) values ('application', 'first-key', 'first-value');
insert into mps.app_settings (application,key,value) values ('application', 'second-key', 'second-value');
insert into mps.app_settings (application,key,value) values ('application', 'third-key', 'third-value');
insert into mps.app_settings (application,key,value) values ('application', 'fifth-key', 'fifth-value');

insert into mps.app_settings (application,key,value) values ('charging', 'second-key', 'second-value-2');
insert into mps.app_settings (application,key,value) values ('charging', 'first-key', 'first-value-2');
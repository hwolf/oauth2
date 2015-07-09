
insert into t_clients (client_id, client_secret, access_token_validity, refresh_token_validity) values ('acme', 'acmesecret', 0, 0);

insert into t_client_entries (client_id, name, data) values ('acme', 'GRANT_TYPE', 'authorization_code');
insert into t_client_entries (client_id, name, data) values ('acme', 'GRANT_TYPE', 'refresh_token');

insert into t_client_entries (client_id, name, data) values ('acme', 'AUTHORITY', 'authority1');
insert into t_client_entries (client_id, name, data) values ('acme', 'AUTHORITY', 'authority2');

insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope1');
insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope2');
insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope3');

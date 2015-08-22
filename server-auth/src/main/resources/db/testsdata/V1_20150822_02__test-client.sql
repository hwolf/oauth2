
insert into t_clients (client_id, client_secret) values ('acme', 'acmesecret');	
insert into t_client_entries (client_id, name, data) values ('acme', 'GRANT_TYPE', 'authorization_code');
insert into t_client_entries (client_id, name, data) values ('acme', 'GRANT_TYPE', 'refresh_token');
insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope1');
insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope2');
insert into t_client_entries (client_id, name, data) values ('acme', 'SCOPE', 'scope3');


insert into t_clients (client_id, client_secret, web_server_redirect_uri)
	values ('acme', 'acmesecret', null);

insert into t_client_authorized_grant_types (client_id, authorized_grant_type) values ('acme', 'authorization_code');
insert into t_client_authorized_grant_types (client_id, authorized_grant_type) values ('acme', 'refresh_token');

insert into t_client_authorities (client_id, authority) values ('acme', 'authority1');
insert into t_client_authorities (client_id, authority) values ('acme', 'authority2');

insert into t_client_scopes (client_id, scope, approved) values ('acme', 'scope1', false);
insert into t_client_scopes (client_id, scope, approved) values ('acme', 'scope2', false);
insert into t_client_scopes (client_id, scope, approved) values ('acme', 'scope3', true);

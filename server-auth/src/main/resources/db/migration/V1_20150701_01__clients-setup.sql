
create table t_clients (
	client_id varchar(50) not null primary key,
	client_secret VARCHAR(500),
  	web_server_redirect_uri varchar(256),
  	access_token_validity integer,
  	refresh_token_validity integer
);

create table t_client_authorized_grant_types (
	client_id varchar(50) not null,
	authorized_grant_type varchar(50) not null
);

create unique index ix_client_authorized_grant_types on t_client_authorized_grant_types (client_id, authorized_grant_type);

create table t_client_scopes (
	client_id varchar(50) not null,
	scope varchar(50) not null,
 	approved boolean not null
);

create unique index ix_client_scope on t_client_scopes (client_id, scope);

create table t_client_resources (
	client_id varchar(50) not null,
	resource_id varchar(50) not null
);

create unique index ix_client_resource_id on t_client_resources (client_id, resource_id);

create table t_client_authorities (
	client_id varchar(50) not null,
	authority varchar(50) not null
);

create unique index ix_client_authority on t_client_authorities (client_id, authority);

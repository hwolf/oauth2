
create table t_access_tokens (
	authentication_id varchar(256),
	user_id varchar(20),
	client_id varchar(20) not null,

	refresh_token varchar(256),

	token_id varchar(256),
	token BLOB,
	authentication BLOB,

	primary key (authentication_id)
);

create table t_refresh_tokens (
	token_id varchar(256),
	token BLOB,
	authentication BLOB,

	primary key (token_id)
);

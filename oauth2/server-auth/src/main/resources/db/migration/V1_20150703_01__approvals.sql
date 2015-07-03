create table t_oauth_approvals (
	user_id varchar(50) not null,
	client_id VARCHAR(50) not null,
	scope varchar(50),
	status varchar(10),
	expires_at timestamp,
	last_modified_at timestamp
);

create unique index ix_oauth_approvals on t_oauth_approvals (user_id, client_id, scope);

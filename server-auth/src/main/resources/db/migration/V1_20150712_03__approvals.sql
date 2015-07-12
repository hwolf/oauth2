
create table t_approvals (
	user_id varchar(20) not null,
	client_id varchar(20) not null,
	scope varchar(50) not null,
	status varchar(10) not null,
	expires_at timestamp,
	last_update_at timestamp,
	primary key (user_id, client_id, scope)
);

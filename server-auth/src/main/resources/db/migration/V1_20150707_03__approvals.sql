
create table t_approvals (
	user_id varchar(50) not null,
	client_id varchar(50) not null,
	scope varchar(50),
	status varchar(10),
	expires_at timestamp,
	last_modified_at timestamp,
		
	primary key (user_id, client_id, scope)
);


create table t_users (
	user_id varchar(50) not null,
	password varchar(500) not null, 
	password_expired timestamp not null,
	
	primary key (user_id)
);

create table t_user_entries (
	user_id varchar(50) not null,
	name varchar(20) not null, 
	data varchar(256) not null,
	
	primary key (user_id, name, data)
);

alter table t_user_entries 
	add constraint fk_user_entries_user_id foreign key (user_id) references t_users (user_id);

create table t_login_status (
	user_id varchar(50) not null,
	failed_login_attempts integer not null, 
	last_failed_login timestamp, 
	last_successful_login timestamp,
	
	primary key (user_id)
);

alter table t_login_status 
	add constraint fk_login_status_user foreign key (user_id) references t_users (user_id);

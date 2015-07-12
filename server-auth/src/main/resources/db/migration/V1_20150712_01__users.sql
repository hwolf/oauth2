
create table t_login_status (
	user_id varchar(20) not null, 
	failed_login_attempts integer not null, 
	last_failed_login timestamp, 
	last_successful_login timestamp, 
	primary key (user_id)
);

create table t_users (
	user_id varchar(20) not null, 
	password varchar(500) not null, 
	password_expires_at timestamp, 
	primary key (user_id)
);

alter table t_users 
	add constraint fk_t_users_login_status foreign key (user_id) references t_login_status (user_id);

create table t_user_entries (
	user_id varchar(20) not null, 
	name varchar(50) not null, 
	data varchar(500) not null
);

alter table t_user_entries 
	add constraint fk_user_entries_users foreign key (user_id) references t_users (user_id);


create table t_users(
	username varchar(50) not null primary key ,
	password varchar(500) not null,
	password_expired datetime not null,
	login_attempts int not null,	
	last_successfull_login datetime,
	last_failed_login datetime
);

create table t_user_authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_user_authorities_users foreign key(username) references t_users(username)
);

create unique index ix_auth_username on t_user_authorities (username, authority);


insert into t_users (username, password, password_expired, login_attempts)
	values ('admin', 'admin', now(), 0);
	
insert into t_user_authorities (username, authority)
	values ('admin', 'ROLE_ADMIN');
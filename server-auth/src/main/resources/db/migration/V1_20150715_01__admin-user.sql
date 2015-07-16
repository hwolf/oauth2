
insert into t_users (user_id, password, password_expires_at)
	values ('admin', 'admin', DATE_ADD(now(), interval 100 day));	
insert into t_user_entries (user_id, name, data) values ('admin', 'AUTHORITY', 'ROLE_ADMIN');
insert into t_user_entries (user_id, name, data) values ('admin', 'AUTHORITY', 'ROLE_ADMIN2');

insert into t_login_status (user_id, failed_login_attempts) values ('admin', 0);


insert into t_users (user_id, password, password_expires_at)
	values ('user1', 'user1', DATE_ADD(now(), interval 1 day));	
insert into t_user_entries (user_id, name, data) values ('user1', 'AUTHORITY', 'ROLE_authenticated');

insert into t_login_status (user_id, failed_login_attempts) values ('user1', 0);

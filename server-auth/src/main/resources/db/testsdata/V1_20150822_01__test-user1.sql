
insert into t_users (user_id, password, password_expires_at)
	values ('testuser1', 'testuser1', DATE_ADD(now(), interval 1 day));	
insert into t_user_entries (user_id, name, data) values ('testuser1', 'AUTHORITY', 'ROLE_authenticated');

insert into t_login_status (user_id, failed_login_attempts) values ('testuser1', 0);

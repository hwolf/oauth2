
insert into t_login_status (user_id, failed_login_attempts) value ('admin', 0);

insert into t_users (user_id, password, password_expires_at)
	values ('admin', 'admin', DATE_ADD(now(), interval 100 day));	
insert into t_user_entries (user_id, name, data) values ('admin', 'ROLE', 'ADMIN');
insert into t_user_entries (user_id, name, data) values ('admin', 'ROLE', 'ADMIN2');


insert into t_login_status (user_id, failed_login_attempts) value ('user1', 0);

insert into t_users (user_id, password, password_expires_at)
	values ('user1', 'user1', DATE_ADD(now(), interval 100 day));	
insert into t_user_entries (user_id, name, data) values ('user1', 'ROLE', 'authenticated');


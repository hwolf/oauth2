
delete from t_login_status where user_id = 'admin1';
delete from t_user_entries where user_id = 'admin1';
delete from t_users where user_id = 'admin1';

insert into t_users (user_id, password, password_expired)
	values ('admin1', 'admin1', DATE_ADD(now(), interval 100 day));	
insert into t_user_entries (user_id, name, data) values ('admin1', 'ROLE', 'ADMIN');

insert into t_login_status (user_id, failed_login_attempts) value ('admin1', 0);

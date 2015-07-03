
delete from t_user_authorities where username = 'admin1';
delete from t_users where username = 'admin1';

insert into t_users (username, password, password_expired, login_attempts)
	values ('admin1', 'admin1', ADD_MONTHS(now(), 2), 0);	
insert into t_user_authorities (username, authority) values ('admin1', 'ROLE_ADMIN');

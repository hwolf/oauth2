
insert into t_users (username, password, password_expired, login_attempts)
	values ('user1', 'user1', DATE_ADD(now(), interval 100 day), 0);	
insert into t_user_authorities (username, authority) values ('user1', 'authenticated');


insert into t_users (username, password, password_expired, login_attempts)
	values ('admin1', 'admin1', DATE_ADD(now(), interval 100 day), 0);	
insert into t_user_authorities (username, authority) values ('admin1', 'ROLE_ADMIN');

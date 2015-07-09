
create table t_clients (
	client_id varchar(50) not null,
	client_secret varchar(500) not null, 
	
	access_token_validity integer not null,
	refresh_token_validity integer not null,
		
	primary key (client_id)
);

create table t_client_entries (
	client_id varchar(50) not null,
	name varchar(20) not null, 
	data varchar(256) not null,
	
	primary key (client_id, name, data)
);

alter table t_client_entries 
	add constraint fk_client_entries_user_id foreign key (client_id) references t_clients (client_id);


create table t_clients (
	client_id varchar(20) not null,
	client_secret varchar(500) not null, 
	access_token_validity integer,
	refresh_token_validity integer,
	primary key (client_id)
);

create table t_client_entries (
	client_id varchar(20) not null,
	name varchar(50) not null, 
	data varchar(500) not null
);

alter table t_client_entries 
	add constraint fk_client_entries_clients foreign key (client_id) references t_clients (client_id);

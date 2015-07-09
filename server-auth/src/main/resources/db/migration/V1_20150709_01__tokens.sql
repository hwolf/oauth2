
create table t_access_tokens (
  token_id varchar(256),
  token MEDIUMBLOB,
  authentication_id varchar(256) primary key,
  user_id varchar(50),
  client_id varchar(50),
  authentication MEDIUMBLOB,
  refresh_token varchar(256)
);

create table t_refresh_tokens (
  token_id varchar(256),
  token MEDIUMBLOB,
  authentication MEDIUMBLOB
);

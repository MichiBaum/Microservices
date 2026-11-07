create table user_credentials (
    id uuid not null,
    user_id varchar(255) not null,
    username varchar(255) not null,
    password_hash varchar(255) not null,
    enabled boolean not null default true,
    created_at datetime(6) not null,
    primary key (id),
    constraint uk_user_credentials_username unique (username)
);

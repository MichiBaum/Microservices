create table authentication_attempt (
    id uuid not null,
    date datetime(6) not null,
    primary key (id)
);

create table jwt (
    id uuid not null,
    jwt MEDIUMTEXT not null,
    primary key (id)
);

create table basic_authentication_attempt (
    authentication_attempt_id uuid not null,
    jwt_id uuid,
    dtype varchar(31) not null,
    user_id varchar(255) not null default '',
    user_name_input varchar(255) not null,
    primary key (authentication_attempt_id),
    constraint fk_basic_auth_attempt_auth_attempt
        foreign key (authentication_attempt_id)
        references authentication_attempt (id),
    constraint fk_basic_auth_attempt_jwt
        foreign key (jwt_id)
        references jwt (id),
    constraint chk_basic_auth_attempt_dtype
        check (dtype in ('BasicAuthenticationAttempt', 'failure', 'success'))
);
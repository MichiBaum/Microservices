create table authentication_attempt
(
    date datetime(6) not null,
    id uuid not null primary key
);

create table basic_authentication_attempt
(
    id uuid not null primary key,
    user_name varchar(255) not null,
    constraint fk_basic_authentication_attempt_authentication_attempt_id foreign key (id) references authentication_attempt (id)
);

create table authentication_success
(
    id uuid not null primary key,
    jwt mediumtext not null,
    user_id varchar(255) not null,
    constraint fk_authentication_success_basic_authentication_attempt_id foreign key (id) references basic_authentication_attempt (id)
);

create table basic_authentication_failure
(
    id uuid not null primary key,
    constraint fk_basic_authentication_failure_basic_authentication_attempt_id foreign key (id) references basic_authentication_attempt (id)
);


create table spotifyoauth_credentials
(
    id uuid not null primary key,
    deactivated bit not null,
    expires_in int not null,
    created_date datetime(6) not null,
    valid_until datetime(6) not null,
    refresh_token varchar(255) not null,
    scope varchar(255) not null,
    user_id varchar(255) not null,
    access_token text not null,
    constraint UK5lca5huyguwx5xarhqnv6pm6o unique (refresh_token),
    constraint UKr4v55p8xpmjmirddvqe44pm0q unique (access_token) using hash
);

create table spotifyoauth_data
(
    id uuid not null primary key,
    state varchar(255) not null,
    user_id varchar(255) not null,
    constraint UKjd13gmmpei055slhjqok0m35t unique (state)
);


create table event
(
    id uuid not null primary key,
    date_from date not null,
    date_to date not null,
    embed_url varchar(255) null,
    location varchar(255) null,
    title varchar(255) not null,
    url varchar(255) null
);

create table event_category
(
    id uuid not null primary key,
    description varchar(255) not null,
    name varchar(255) not null
);

create table event_category_mapping
(
    category_id uuid not null,
    event_id uuid not null,
    primary key (category_id, event_id),
    constraint FK5dakbhiaox4y3375rth9tc2sg foreign key (category_id) references event_category (id),
    constraint FKppj0dpllus3pjunrf6ulykq8r foreign key (event_id) references event (id)
);

create table game
(
    event_id uuid null,
    id uuid not null primary key,
    opening_name varchar(255) null,
    platform_id varchar(255) not null,
    chess_platform enum('CHESSCOM', 'LICHESS', 'OVER_THE_BOARD') null,
    game_type enum('BLITZ', 'BULLET', 'CLASSICAL', 'RAPID', 'UNKNOWN') null,
    pgn text not null,
    constraint FKnqufihgcswqe5fvhkfpuj7201 foreign key (event_id) references event (id)
);

create table person
(
    id uuid not null primary key,
    birthday date null,
    federation varchar(255) null,
    fide_id varchar(255) null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    gender enum('FEMALE', 'MALE', 'UNKNOWN') not null
);

create table account
(
    id uuid not null primary key,
    created_at date null,
    person_id uuid null,
    name varchar(255) not null,
    platform_id varchar(255) not null,
    url varchar(255) not null,
    username varchar(255) not null,
    platform enum('CHESSCOM', 'LICHESS', 'OVER_THE_BOARD') null,
    constraint FKd9dhia7smrg88vcbiykhofxee foreign key (person_id) references person (id)
);

create table account_game_mapping
(
    account_id uuid not null,
    game_id uuid not null,
    primary key (account_id, game_id),
    constraint FK3xxnwfoji9a57q5gp55cxwip4 foreign key (game_id) references game (id),
    constraint FKh3ff1qmiwes4gh1venqmh1v57 foreign key (account_id) references account (id)
);

create table event_participants_mapping
(
    event_id uuid not null,
    person_id uuid not null,
    primary key (event_id, person_id),
    constraint FKmaq2jiv6ns0rsyu4bqr3ps225 foreign key (event_id) references event (id),
    constraint FKq0tjsagris7sowbyylwn6ckma foreign key (person_id) references person (id)
);

create table player
(
    id uuid not null primary key,
    rating bigint not null,
    game_id uuid not null,
    platform_id varchar(255) not null,
    username varchar(255) not null,
    piece_color enum('BLACK', 'WHITE') null,
    constraint FK8095bt0vv5capccv9870ln2n foreign key (game_id) references game (id)
);
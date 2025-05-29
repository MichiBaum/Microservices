create table magazine
(
    id uuid not null primary key,
);

create table book
(
    id uuid not null primary key,
    title varchar(255) not null
);

create table list_of_contents
(
    id uuid not null primary key,
);

create table news_article
(
    id uuid not null primary key,
);

create table author
(
    id uuid not null primary key,
);

create table news_category
(
    id uuid not null primary key,
);

create table news_paper
(
    id uuid not null primary key,
);

create table news_tag
(
    id uuid not null primary key,
);

create table note
(
    id uuid not null primary key,
);



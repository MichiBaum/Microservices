create table magazine
(
    id uuid not null primary key
);

create table magazine_article
(
    id uuid not null primary key,
    title varchar(255) not null
);

create table book
(
    id uuid not null primary key,
    title varchar(255) not null
);

create table book_publisher
(
    id uuid not null primary key
);

create table book_review
(
    id uuid not null primary key
);

create table news_article
(
    id uuid not null primary key
);

create table author
(
    id uuid not null primary key
);

create table news_category
(
    id uuid not null primary key
);

create table news_paper
(
    id uuid not null primary key
);

create table news_tag
(
    id uuid not null primary key
);

create table note
(
    id uuid not null primary key
);



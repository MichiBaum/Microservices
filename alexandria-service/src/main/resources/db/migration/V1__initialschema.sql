create table content_language
(
    id       uuid              not null primary key,
    language enum ('DE', 'EN') not null
);


-- Author
create table author
(
    id uuid not null primary key,
    name       varchar(255) null
);


-- Blog
create table blog_article
(
    id        uuid         not null primary key,
    author_id varchar(255) not null,
    content   varchar(255) not null,
    title     varchar(255) not null
);


-- Book
create table book
(
    author_id uuid         not null,
    id        uuid         not null primary key,
    title     varchar(255) not null,
    
    constraint fk_book_author_id foreign key (author_id) references author (id)
);

create table book_list_of_contents_element
(
    page_number int          null,
    book_id     uuid         not null,
    id          uuid         not null primary key,
    title       varchar(255) null,
    
    constraint fk_book_list_of_contents_element_book_id foreign key (book_id) references book (id)
);

create table book_publisher
(
    id uuid not null primary key
);

create table book_publisher_mapping
(
    book_id     uuid not null,
    category_id uuid not null,
    primary key (book_id, category_id),
    
    constraint fk_book_publisher_mapping_book_publisher_id foreign key (category_id) references book_publisher (id),
    constraint fk_book_publisher_mapping_book_id foreign key (book_id) references book (id)
);

create table book_review
(
    book_id uuid not null,
    id      uuid not null,
    primary key (book_id, id),
    constraint fk_book_review_book_id foreign key (book_id) references book (id)
);


-- Magazine
create table magazine
(
    id uuid not null primary key
);

create table magazine_article
(
    id    uuid         not null primary key,
    title varchar(255) not null
);

create table magazine_list_of_contents_element
(
    id          uuid not null primary key,
    page_number int  null,
    article_id  uuid not null,
    magazine_id uuid not null,
    constraint fk_magazine_list_of_contents_element_magazine_article_id foreign key (article_id) references magazine_article (id),
    constraint fk_magazine_list_of_contents_element_magazine_id foreign key (magazine_id) references magazine (id)
);


-- News
create table news_article
(
    author_id uuid not null,
    id        uuid not null primary key,
    constraint fk_news_article_author_id foreign key (author_id) references author (id)
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


-- Note
create table note
(
    deleted    bit          null,
    encrypted  bit          null,
    id         uuid         not null primary key,
    belongs_to varchar(255) null,
    text       varchar(255) null,
    title      varchar(255) null
);

create table revinfo
(
    rev      int auto_increment primary key,
    revtstmp bigint null
);

create table note_aud
(
    deleted    bit          null,
    encrypted  bit          null,
    rev        int          not null,
    revtype    tinyint      null,
    id         uuid         not null,
    belongs_to varchar(255) null,
    text       varchar(255) null,
    title      varchar(255) null,
    
    primary key (rev, id),
    constraint fk_note_aud_revinfo_rev foreign key (rev) references revinfo (rev)
);

create table note_category
(
    id          uuid         not null primary key,
    belongs_to  varchar(255) null,
    description varchar(255) null,
    title       varchar(255) null
);

create table note_category_mapping
(
    category_id uuid not null,
    note_id     uuid not null,
    primary key (category_id, note_id),
    
    constraint fk_note_category_mapping_note_category_id foreign key (category_id) references note_category (id),
    constraint fk_note_category_mapping_note_id foreign key (note_id) references note (id)
);
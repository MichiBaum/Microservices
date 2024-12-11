create table permission
(
    permission varchar(255) not null primary key
);

INSERT INTO permission (permission) VALUES ('ADMIN_SERVICE');
INSERT INTO permission (permission) VALUES ('AUTHENTICATION_SERVICE');
INSERT INTO permission (permission) VALUES ('CHESS_SERVICE');
INSERT INTO permission (permission) VALUES ('CHESS_SERVICE_ADMIN');
INSERT INTO permission (permission) VALUES ('FITNESS_SERVICE');
INSERT INTO permission (permission) VALUES ('MUSIC_SERVICE');
INSERT INTO permission (permission) VALUES ('REGISTRY_SERVICE');
INSERT INTO permission (permission) VALUES ('SERMANAGEMENT_SERVICE_EDIT_ALL_USER');
INSERT INTO permission (permission) VALUES ('USERMANAGEMENT_SERVICE');
INSERT INTO permission (permission) VALUES ('USERMANAGEMENT_SERVICE_EDIT_OWN_USER');

create table user
(
    id uuid not null primary key,
    email varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null,
    constraint UKob8kqyqqgmefl0aco34akdtpe unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx unique (username)
);

create table user_permission_mapping
(
    user_id uuid not null,
    permission_id varchar(255) not null,
    primary key (user_id, permission_id),
    constraint FKfd079v5t2dt0yj8c6fodrf8ml foreign key (user_id) references user (id),
    constraint FKihjegis3tdwmlnawifkuqkav9 foreign key (permission_id) references permission (permission)
);


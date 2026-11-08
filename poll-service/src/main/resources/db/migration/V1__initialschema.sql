-- Initial schema for poll-service

create table poll
(
    id uuid not null primary key,
    title varchar(255) not null,
    description text null,
    slug varchar(64) not null,
    creator_id varchar(255) not null,
    status enum('DRAFT', 'PUBLISHED', 'CLOSED') not null,
    access_policy enum('OPEN', 'INVITED_ONLY', 'SECRET_PROTECTED') not null,
    secret varchar(255) null,
    is_anonymous bit not null,
    thank_you_message text null,
    redirect_url varchar(2048) null,
    expires_at datetime(6) null,
    deleted_at datetime(6) null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint UK_poll_slug unique (slug)
);

create table poll_invitee
(
    id uuid not null primary key,
    poll_id uuid not null,
    user_id varchar(255) not null,
    created_at datetime(6) not null,
    constraint UK_poll_invitee unique (poll_id, user_id),
    constraint FK_poll_invitee_poll foreign key (poll_id) references poll (id)
);

create table poll_section
(
    id uuid not null primary key,
    poll_id uuid not null,
    title varchar(255) not null,
    description text null,
    order_index int not null,
    constraint FK_poll_section_poll foreign key (poll_id) references poll (id)
);

create table poll_question
(
    id uuid not null primary key,
    section_id uuid not null,
    title varchar(255) not null,
    description text null,
    type enum('SINGLE_SELECT', 'MULTI_SELECT', 'TEXT', 'NUMERICAL', 'DATE', 'MULTI_ENTRY') not null,
    is_required bit not null,
    order_index int not null,
    constraint FK_poll_question_section foreign key (section_id) references poll_section (id)
);

create table question_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_length int null,
    max_length int null,
    pattern text null,
    format_preset enum('EMAIL', 'URL', 'PHONE', 'ALPHANUMERIC') null,
    min_value double null,
    max_value double null,
    integer_only bit null,
    min_date date null,
    max_date date null,
    disable_past bit null,
    min_items int null,
    max_items int null,
    constraint UK_question_validator_question unique (question_id),
    constraint FK_question_validator_question foreign key (question_id) references poll_question (id)
);

create table question_option
(
    id uuid not null primary key,
    question_id uuid not null,
    title varchar(255) not null,
    description text null,
    order_index int not null,
    constraint FK_question_option_question foreign key (question_id) references poll_question (id)
);

create table poll_submission
(
    id uuid not null primary key,
    poll_id uuid not null,
    user_id varchar(255) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint UK_poll_submission_poll_user unique (poll_id, user_id),
    constraint FK_poll_submission_poll foreign key (poll_id) references poll (id)
);

create table question_answer_value
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    option_id uuid null,
    text_value text null,
    numeric_value double null,
    date_value date null,
    order_index int null,
    constraint FK_answer_value_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_answer_value_question foreign key (question_id) references poll_question (id),
    constraint FK_answer_value_option foreign key (option_id) references question_option (id)
);

create index idx_poll_creator_id on poll (creator_id);
create index idx_poll_invitee_user_id on poll_invitee (user_id);
create index idx_poll_submission_poll_id on poll_submission (poll_id);
create index idx_poll_submission_user_id on poll_submission (user_id);
create index idx_answer_value_question_id on question_answer_value (question_id);
create index idx_answer_value_submission_id on question_answer_value (submission_id);
create index idx_answer_value_date_value on question_answer_value (date_value);

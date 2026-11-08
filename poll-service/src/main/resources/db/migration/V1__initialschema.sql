-- Initial schema for poll-service with table-per-entity inheritance

create table open_poll
(
    id uuid not null primary key,
    title varchar(255) not null,
    description text null,
    slug varchar(64) not null,
    creator_id varchar(255) not null,
    status enum('DRAFT', 'PUBLISHED', 'CLOSED') not null,
    is_anonymous bit not null,
    expires_at datetime(6) null,
    deleted_at datetime(6) null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint UK_open_poll_slug unique (slug)
);

create table invite_poll
(
    id uuid not null primary key,
    title varchar(255) not null,
    description text null,
    slug varchar(64) not null,
    creator_id varchar(255) not null,
    status enum('DRAFT', 'PUBLISHED', 'CLOSED') not null,
    is_anonymous bit not null,
    expires_at datetime(6) null,
    deleted_at datetime(6) null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint UK_invite_poll_slug unique (slug)
);

create table secret_poll
(
    id uuid not null primary key,
    title varchar(255) not null,
    description text null,
    slug varchar(64) not null,
    creator_id varchar(255) not null,
    status enum('DRAFT', 'PUBLISHED', 'CLOSED') not null,
    secret varchar(255) not null,
    is_anonymous bit not null,
    expires_at datetime(6) null,
    deleted_at datetime(6) null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    constraint UK_secret_poll_slug unique (slug)
);

create table poll_invitee
(
    id uuid not null primary key,
    poll_id uuid not null,
    user_id varchar(255) not null,
    created_at datetime(6) not null,
    constraint UK_poll_invitee unique (poll_id, user_id),
    constraint FK_poll_invitee_poll foreign key (poll_id) references invite_poll (id)
);

create table poll_section
(
    id uuid not null primary key,
    poll_id uuid not null,
    title varchar(255) not null,
    description text null,
    order_index int not null
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

create table text_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_length int null,
    max_length int null,
    pattern text null,
    format_preset enum('EMAIL', 'URL', 'PHONE', 'ALPHANUMERIC') null,
    constraint UK_text_validator_question unique (question_id),
    constraint FK_text_validator_question foreign key (question_id) references poll_question (id)
);

create table numerical_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_value double null,
    max_value double null,
    integer_only bit not null,
    constraint UK_numerical_validator_question unique (question_id),
    constraint FK_numerical_validator_question foreign key (question_id) references poll_question (id)
);

create table date_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_date date null,
    max_date date null,
    disable_past bit not null,
    constraint UK_date_validator_question unique (question_id),
    constraint FK_date_validator_question foreign key (question_id) references poll_question (id)
);

create table multi_select_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_selected int null,
    max_selected int null,
    constraint UK_multi_select_validator_question unique (question_id),
    constraint FK_multi_select_validator_question foreign key (question_id) references poll_question (id)
);

create table multi_entry_validator
(
    id uuid not null primary key,
    question_id uuid not null,
    min_items int null,
    max_items int null,
    constraint UK_multi_entry_validator_question unique (question_id),
    constraint FK_multi_entry_validator_question foreign key (question_id) references poll_question (id)
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
    constraint UK_poll_submission_poll_user unique (poll_id, user_id)
);

create table single_select_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    selected_option_id uuid not null,
    created_at datetime(6) not null,
    constraint FK_single_select_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_single_select_answer_question foreign key (question_id) references poll_question (id),
    constraint FK_single_select_answer_option foreign key (selected_option_id) references question_option (id)
);

create table multi_select_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    selected_option_id uuid not null,
    created_at datetime(6) not null,
    constraint FK_multi_select_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_multi_select_answer_question foreign key (question_id) references poll_question (id),
    constraint FK_multi_select_answer_option foreign key (selected_option_id) references question_option (id)
);

create table text_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    text_value text not null,
    created_at datetime(6) not null,
    constraint FK_text_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_text_answer_question foreign key (question_id) references poll_question (id)
);

create table numerical_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    numeric_value double not null,
    created_at datetime(6) not null,
    constraint FK_numerical_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_numerical_answer_question foreign key (question_id) references poll_question (id)
);

create table date_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    date_value date not null,
    created_at datetime(6) not null,
    constraint FK_date_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_date_answer_question foreign key (question_id) references poll_question (id)
);

create table multi_entry_answer
(
    id uuid not null primary key,
    submission_id uuid not null,
    question_id uuid not null,
    text_value text not null,
    order_index int not null,
    created_at datetime(6) not null,
    constraint FK_multi_entry_answer_submission foreign key (submission_id) references poll_submission (id),
    constraint FK_multi_entry_answer_question foreign key (question_id) references poll_question (id)
);

create index idx_open_poll_creator_id on open_poll (creator_id);
create index idx_invite_poll_creator_id on invite_poll (creator_id);
create index idx_secret_poll_creator_id on secret_poll (creator_id);
create index idx_poll_invitee_user_id on poll_invitee (user_id);
create index idx_poll_section_poll_id on poll_section (poll_id);
create index idx_poll_submission_poll_id on poll_submission (poll_id);
create index idx_poll_submission_user_id on poll_submission (user_id);

create index idx_single_select_answer_sub on single_select_answer (submission_id);
create index idx_single_select_answer_q on single_select_answer (question_id);
create index idx_multi_select_answer_sub on multi_select_answer (submission_id);
create index idx_multi_select_answer_q on multi_select_answer (question_id);
create index idx_text_answer_sub on text_answer (submission_id);
create index idx_text_answer_q on text_answer (question_id);
create index idx_numerical_answer_sub on numerical_answer (submission_id);
create index idx_numerical_answer_q on numerical_answer (question_id);
create index idx_date_answer_sub on date_answer (submission_id);
create index idx_date_answer_q on date_answer (question_id);
create index idx_date_answer_date_value on date_answer (date_value);
create index idx_multi_entry_answer_sub on multi_entry_answer (submission_id);
create index idx_multi_entry_answer_q on multi_entry_answer (question_id);

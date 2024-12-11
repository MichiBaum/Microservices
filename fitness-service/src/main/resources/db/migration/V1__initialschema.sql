create table fitbit_subscription
(
    id uuid not null primary key,
    user_id varchar(255) not null,
    verification_code varchar(255) not null
);

create table fitbit_notification
(
    id uuid not null primary key,
    collection_type enum('ACTIVITIES', 'BODY', 'FOODS', 'SLEEP', 'USER_REVOKED_ACCESS') null,
    subscription_id uuid not null,
    date varchar(255) not null,
    owner_id varchar(255) not null,
    owner_type varchar(255) not null,
    user_id varchar(255) not null,
    constraint FKpsfb0bejhn7dc8wcv83owosdt foreign key (subscription_id) references fitbit_subscription (id)
);

create table fitbitoauth_credentials
(
    id uuid not null primary key,
    deactivated bit not null,
    expires_in int not null,
    created_date datetime(6) not null,
    valid_until datetime(6) not null,
    fitbit_user_id varchar(255) not null,
    refresh_token varchar(255) not null,
    scope varchar(255) not null,
    user_id varchar(255) not null,
    access_token text not null,
    constraint UKcfsdfq1li2un48pctkiqgfos3 unique (access_token) using hash,
    constraint UKseja4y645trnxbi76bw98883t unique (refresh_token)
);

create table fitbitoauth_data
(
    id uuid not null primary key,
    code_challenge varchar(255) not null,
    code_verifier varchar(255) not null,
    state varchar(255) not null,
    user_id varchar(255) not null,
    constraint UK5a1wvv90ubjq9fp5aq8othp3 unique (code_verifier),
    constraint UK8lohmmv4rcjqfgyvq9mqo45ia unique (code_challenge),
    constraint UKgjukj60ntstleroy47u6joo3y unique (state)
);

create table profile
(
    id uuid not null primary key,
    height double not null,
    dtype varchar(31) not null,
    age varchar(255) not null,
    country varchar(255) not null,
    full_name varchar(255) not null,
    gender varchar(255) not null,
    user_id varchar(255) not null
);

create table sleep
(
    id uuid not null primary key,
    duration bigint not null,
    end_time datetime(6) not null,
    fitbit_id bigint not null,
    start_time datetime(6) not null,
    dtype varchar(31) not null,
    user_id varchar(255) not null,
    constraint UKeixguljyuktx0nrl2trwpv8s4 unique (fitbit_id)
);

create table sleep_stage
(
    id uuid not null primary key,
    duration bigint not null,
    end datetime(6) not null,
    start datetime(6) not null,
    sleep_id uuid null,
    stage enum('ASLEEP', 'AWAKE', 'DEEP', 'LIGHT', 'REM', 'RESTLESS', 'UNKNOWN', 'WAKE') null,
    constraint FK833rltsygocx618j999rn4wc3 foreign key (sleep_id) references sleep (id)
);

create table weight
(
    id uuid not null primary key,
    bmi double not null,
    fat_percentage int null,
    weight double not null,
    date datetime(6) not null,
    fitbit_id bigint not null,
    dtype varchar(31) not null,
    user_id varchar(255) not null,
    constraint UKpatau0u712jsiniwhrw7d9krm unique (fitbit_id)
);


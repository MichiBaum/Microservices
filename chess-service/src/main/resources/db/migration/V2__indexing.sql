create index person_firstname_index on person (firstname);
create index person_lastname_index on person (lastname);
create index person_firstname_lastname_index on person (lastname, firstname);

create index account_username_index on account (username);
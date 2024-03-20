create table setting
(
    id       varchar(32) primary key,
    language varchar(32),
    theme    varchar(32)
);

create table cluster
(
    id         varchar(32) primary key,
    name       varchar(512),
    servers    varchar(512)
);
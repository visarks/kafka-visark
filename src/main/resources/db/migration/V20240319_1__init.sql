create table setting
(
    id         varchar(32) primary key,
    language   varchar(32),
    theme      varchar(32),
    timeout    int,
    autoTheme  bool,
    openDialog bool
);

create table cluster
(
    id      varchar(32) primary key,
    name    varchar(512),
    servers varchar(512),
    security bool,
    protocal varchar(32),
    mechanism varchar(32),
    username varchar(512),
    password varchar(512)
);

create table topic_setting
(
    id                varchar(32) primary key,
    clusterId         varchar(32),
    topic             varchar(512),
    keyDeserializer   varchar(32),
    valueDeserializer varchar(32)
);
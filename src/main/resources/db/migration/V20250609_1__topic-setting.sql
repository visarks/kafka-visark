drop table if exists topic_setting;
create table topic_setting
(
    id           text primary key,
    clusterId    text,
    topic        text,
    keyType      text,
    keyCharset   text,
    valueType    text,
    valueCharset text
);
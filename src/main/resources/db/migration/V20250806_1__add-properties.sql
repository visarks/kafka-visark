alter table cluster add column properties text;

update cluster set properties='{}';
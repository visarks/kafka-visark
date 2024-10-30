alter table cluster add column type varchar(32);
alter table cluster add column parentId varchar(32);
update cluster set type='cluster';

create table server_config
(
    id          int unsigned auto_increment primary key,
    server_name varchar(255) not null comment '服务名'
);
create table spring_config
(
    id            int unsigned auto_increment primary key,
    server_id   int unsigned not null,
    config_key    varchar(255) not null comment '',
    value         varchar(255) not null comment '',
    create_time   datetime,
    update_time   datetime,
    description text
);
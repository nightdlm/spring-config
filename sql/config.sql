CREATE TABLE `server_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_name` varchar(255)     NOT NULL COMMENT '服务名',
    create_time   datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT='服务器配置表';

CREATE TABLE `spring_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_id`   int(10) unsigned NOT NULL COMMENT '服务ID，关联server_config表',
    `config_key`  varchar(255)     NOT NULL COMMENT '配置键',
    `value`       varchar(255)     NOT NULL COMMENT '配置值',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `description` text COMMENT '配置描述',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT='Spring配置表'

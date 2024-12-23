CREATE TABLE `server_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `server_name` varchar(255)     NOT NULL COMMENT '服务名',
    create_time   datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

CREATE TABLE `spring_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `server_id`   int(10) unsigned NOT NULL,
    `config_key`  varchar(255)     NOT NULL,
    `value`       varchar(255)     NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT NULL,
    `description` text,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
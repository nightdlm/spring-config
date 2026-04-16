CREATE TABLE `server_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_name` varchar(255)     NOT NULL COMMENT '服务名',
    create_time   datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='服务器配置表';

CREATE TABLE `spring_config`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_id`   int(10) unsigned NOT NULL COMMENT '服务ID，关联server_config表',
    `config_key`  varchar(255)     NOT NULL COMMENT '配置键',
    `value`       text             NOT NULL COMMENT '配置值',
    `create_time` datetime   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   DEFAULT NULL COMMENT '更新时间',
    `description` text COMMENT '配置描述',
    `is_deleted`  tinyint(1) DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_server_id` (`server_id`),
    KEY `idx_config_key` (`config_key`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='Spring配置表';

-- 用户表
CREATE TABLE `sys_user`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    varchar(50)      NOT NULL COMMENT '用户名',
    `password`    varchar(255)     NOT NULL COMMENT '密码（加密后）',
    `nickname`    varchar(50)               DEFAULT NULL COMMENT '昵称',
    `email`       varchar(100)              DEFAULT NULL COMMENT '邮箱',
    `role_id`     int(10) unsigned          DEFAULT NULL COMMENT '角色ID，关联sys_role表',
    `status`      tinyint(1)       NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime                  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint(1)                DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统用户表';

-- 系统配置表（用于存储邮箱等系统配置）
CREATE TABLE `sys_config`
(
    `id`           int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key`   varchar(100)     NOT NULL COMMENT '配置键',
    `config_value` text             NOT NULL COMMENT '配置值',
    `description`  varchar(255) DEFAULT NULL COMMENT '配置描述',
    `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   tinyint(1)   DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统配置表';

-- 配置历史表
CREATE TABLE `config_history`
(
    `id`                  int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_id`           int(10) unsigned NOT NULL COMMENT '配置ID，关联spring_config表',
    `server_id`           int(10) unsigned NOT NULL COMMENT '服务ID',
    `config_key`          varchar(255)     NOT NULL COMMENT '配置键',
    `old_value`           text COMMENT '修改前的值',
    `new_value`           text COMMENT '修改后的值',
    `operator_id`         int(10) unsigned NOT NULL COMMENT '操作人ID，关联sys_user表',
    `operator_name`       varchar(50)      NOT NULL COMMENT '操作人姓名',
    `operation_type`      varchar(20)      NOT NULL COMMENT '操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，ROLLBACK-回滚',
    `rollback_version_id` int(10) unsigned DEFAULT NULL COMMENT '回滚时的版本号ID（如果是回滚操作）',
    `create_time`         datetime         DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `remark`              varchar(500)     DEFAULT NULL COMMENT '备注说明',
    `is_deleted`          tinyint(1)       DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_config_id` (`config_id`),
    KEY `idx_server_id` (`server_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='配置历史表';

-- 插入默认管理员账户 (密码: admin123, 使用BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role_id`, `status`)
VALUES ('admin', '$2a$10$BOfLQFLlWsjKDVqj1k3KJOpxZPI9sQJ7gvKZ1BWo86AuEUqksOPEW', '管理员', 1, 1);

-- 角色表
CREATE TABLE `sys_role`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name`   varchar(50)      NOT NULL COMMENT '角色名称',
    `role_code`   varchar(50)      NOT NULL COMMENT '角色编码（唯一）',
    `description` varchar(255)              DEFAULT NULL COMMENT '角色描述',
    `status`      tinyint(1)       NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime                  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint(1)                DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统角色表';

-- 权限表
CREATE TABLE `sys_permission`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `perm_name`   varchar(50)      NOT NULL COMMENT '权限名称',
    `perm_code`   varchar(100)     NOT NULL COMMENT '权限编码（唯一）',
    `perm_type`   varchar(20)      NOT NULL DEFAULT 'MENU' COMMENT '权限类型：MENU-菜单，BUTTON-按钮，API-接口',
    `parent_id`   int(10) unsigned          DEFAULT 0 COMMENT '父权限ID',
    `path`        varchar(255)              DEFAULT NULL COMMENT '路由路径/接口路径',
    `icon`        varchar(50)               DEFAULT NULL COMMENT '图标',
    `sort_order`  int(11)                   DEFAULT 0 COMMENT '排序',
    `status`      tinyint(1)       NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime                  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  tinyint(1)                DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统权限表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission`
(
    `id`            int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`       int(10) unsigned NOT NULL COMMENT '角色ID',
    `permission_id` int(10) unsigned NOT NULL COMMENT '权限ID',
    `create_time`   datetime         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色权限关联表';

-- 插入默认角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`)
VALUES ('超级管理员', 'ADMIN', '拥有所有权限', 1),
       ('普通用户', 'USER', '基本查看和修改权限', 1),
       ('配置管理员', 'CONFIG_MANAGER', '只能管理配置', 1);

-- 插入默认权限
INSERT INTO `sys_permission` (`perm_name`, `perm_code`, `perm_type`, `parent_id`, `path`, `icon`, `sort_order`, `status`)
VALUES 
-- 菜单权限
('服务管理', 'menu:server', 'MENU', 0, '/server', 'Setting', 1, 1),
('配置管理', 'menu:config', 'MENU', 0, '/config', 'Document', 2, 1),
('用户管理', 'menu:user', 'MENU', 0, '/user', 'User', 3, 1),
('系统设置', 'menu:system', 'MENU', 0, '/system', 'Tools', 4, 1),

-- 按钮权限 - 服务管理
('查看服务', 'server:view', 'BUTTON', 1, NULL, NULL, 1, 1),
('创建服务', 'server:create', 'BUTTON', 1, NULL, NULL, 2, 1),
('编辑服务', 'server:edit', 'BUTTON', 1, NULL, NULL, 3, 1),
('删除服务', 'server:delete', 'BUTTON', 1, NULL, NULL, 4, 1),

-- 按钮权限 - 配置管理
('查看配置', 'config:view', 'BUTTON', 2, NULL, NULL, 1, 1),
('创建配置', 'config:create', 'BUTTON', 2, NULL, NULL, 2, 1),
('编辑配置', 'config:edit', 'BUTTON', 2, NULL, NULL, 3, 1),
('删除配置', 'config:delete', 'BUTTON', 2, NULL, NULL, 4, 1),
('发布配置', 'config:publish', 'BUTTON', 2, NULL, NULL, 5, 1),
('查看历史', 'config:history', 'BUTTON', 2, NULL, NULL, 6, 1),
('回滚配置', 'config:rollback', 'BUTTON', 2, NULL, NULL, 7, 1),

-- 按钮权限 - 用户管理
('查看用户', 'user:view', 'BUTTON', 3, NULL, NULL, 1, 1),
('创建用户', 'user:create', 'BUTTON', 3, NULL, NULL, 2, 1),
('编辑用户', 'user:edit', 'BUTTON', 3, NULL, NULL, 3, 1),
('删除用户', 'user:delete', 'BUTTON', 3, NULL, NULL, 4, 1),
('重置密码', 'user:resetPassword', 'BUTTON', 3, NULL, NULL, 5, 1),

-- API权限
('获取配置列表', 'api:config:list', 'API', 0, '/api/config/list/*', NULL, 1, 1),
('创建/更新配置', 'api:config:update', 'API', 0, '/api/config/update', NULL, 2, 1),
('删除配置', 'api:config:delete', 'API', 0, '/api/config/delete/*', NULL, 3, 1),
('发布配置', 'api:config:publish', 'API', 0, '/api/config/publish/*', NULL, 4, 1),
('获取用户列表', 'api:user:list', 'API', 0, '/api/user/list', NULL, 5, 1),
('创建用户', 'api:user:create', 'API', 0, '/api/user/create', NULL, 6, 1),
('删除用户', 'api:user:delete', 'API', 0, '/api/user/delete/*', NULL, 7, 1);

-- 为超级管理员分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission` WHERE is_deleted = 0;

-- 为普通用户分配基本权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `sys_permission` 
WHERE perm_code IN (
    'menu:server', 'menu:config',
    'server:view',
    'config:view', 'config:history',
    'api:config:list'
) AND is_deleted = 0;

-- 为配置管理员分配配置相关权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `sys_permission` 
WHERE perm_code LIKE 'config:%' OR perm_code LIKE 'menu:config' OR perm_code LIKE 'api:config:%'
AND is_deleted = 0;

-- 插入默认系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`)
VALUES ('email.enabled', 'false', '是否启用邮件通知'),
       ('email.smtp.host', '', 'SMTP服务器地址'),
       ('email.smtp.port', '25', 'SMTP服务器端口'),
       ('email.smtp.username', '', 'SMTP用户名'),
       ('email.smtp.password', '', 'SMTP密码'),
       ('email.from', '', '发件人邮箱'),
       ('email.to.admin', '', '管理员接收通知的邮箱');

# 动态配置管理系统

## 项目简介
一个基于Spring Boot 3的配置管理系统，支持动态配置管理、版本控制、邮件通知等功能。

## 主要功能
- ✅ 对static的字段赋值，解决静态变量无法通过@Value直接赋值的问题
- ✅ 支持map、list和基本数据类型配置
- ✅ 远程配置管理，结合Redis实现不重启即可修改配置参数
- ✅ 用户登录认证系统（基于Session + Redis）
- ✅ 角色权限管理（管理员/普通用户）
- ✅ 配置历史版本管理
- ✅ 配置回滚到任意历史版本
- ✅ 配置差异对比（Diff）
- ✅ 邮件通知功能（配置变更时自动通知）
- ✅ 软删除机制

## 技术栈
### 后端
- Spring Boot 3.2.4
- JDK 17
- MyBatis Plus 3.5.5
- MySQL 8.0+
- Redis
- Spring Session
- Spring Security Crypto (密码加密)
- JavaMail (邮件服务)

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios

## 快速开始

### 1. 数据库初始化
执行 `sql/config.sql` 文件创建数据库表结构。

默认管理员账户：
- 用户名: admin
- 密码: admin123

### 2. 配置应用
修改 `config-http/src/main/resources/application.properties`：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/config_center?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379

# 邮箱配置（可选）
# 在系统管理中配置SMTP信息
```

### 3. 启动应用
```bash
cd config-http
mvn spring-boot:run
```

### 4. 访问系统
- 后端API: http://localhost:27369
- 前端页面: 需要先构建前端项目

## 引入依赖

在你的项目中引入动态配置依赖：
```xml
<dependency>
    <groupId>io.github.nightdlm</groupId>
    <artifactId>dynamic-spring-boot-config</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 核心功能说明

### 1. 用户认证
- 基于Session的用户认证
- Session存储在Redis中，支持分布式部署
- BCrypt密码加密

### 2. 配置管理
- 支持配置的增删改查
- 配置发布到Redis实现动态更新
- 所有操作都会记录历史

### 3. 历史版本
- 自动记录每次配置变更
- 支持查看历史版本列表
- 支持回滚到任意历史版本
- 回滚操作也会被记录

### 4. 差异对比
- 可视化展示配置变更前后差异
- 绿色表示新增内容
- 红色表示删除内容

### 5. 邮件通知
- 配置变更时自动发送邮件通知
- 支持开启/关闭邮件通知
- 可配置SMTP服务器信息
- 在系统管理中配置邮箱参数

## 注意事项
- 动态更新配置依赖Redis的发布订阅功能，需要Redis支持
- 建议使用最新版本，避免使用过时的版本
- 生产环境请修改默认管理员密码
- 邮件功能需要正确配置SMTP服务器

## 版本历史
- v1.0.0 (当前版本)
  - 升级到Spring Boot 3
  - 添加用户登录系统
  - 添加配置历史版本管理
  - 添加配置回滚功能
  - 添加Diff对比功能
  - 添加邮件通知功能
  - 添加软删除机制

- v0.1.2
  - 初始版本
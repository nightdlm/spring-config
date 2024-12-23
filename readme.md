# 动态配置
## 目的
- 对static的字段赋值，解决静态变量无法通过@Value无法直接赋值的问题，与@Value使用方式一致
- 支持map，list，和基本数据类型配置
- 有需要可以使用远程配置，结合使用可以实现不重启即可修改配置参数

引入依赖：
```
        <dependency>
            <groupId>io.spring.config</groupId>
            <artifactId>spring-config</artifactId>
            <version>0.1.2</version>
        </dependency>
```
## 项目使用技术版本
- spring-boot 2.6.13
- jdk 1.8
- spring-web 5.3.24
- spring-boot-starter-data-redis 2.6.13
- fastjson2 2.0.31

## 注意
- 动态更新配置依赖了redis的发布订阅功能，需要redis支持
- 尽量使用最新版本，避免使用过时的版本，避免出现不可预知的问题
- 后续增加用户功能
# 动态配置
## 目的
- 统一管理配置，更加简洁明了
- 使用static动态管理配置，实现不用重启服务即可动态修改配置值
- 轻量级服务

引入依赖：
````
        <dependency>
            <groupId>io.spring.config</groupId>
            <artifactId>spring-config</artifactId>
            <version>0.1.0</version>
<!--            不需要使用远程配置的时候需要排除这个，否则需要引入-->
<!--            <exclusions>-->
<!--                <exclusion>-->
<!--                    <groupId>io.spring.config</groupId>-->
<!--                    <artifactId>config-core</artifactId>-->
<!--                </exclusion>-->
<!--            </exclusions>-->
        </dependency>

package io.spring.config.configs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.spring.config.mapper")
public class SpringConfig {

}

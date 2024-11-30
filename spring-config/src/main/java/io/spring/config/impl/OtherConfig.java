package io.spring.config.impl;

import io.spring.config.annotation.EnableRemoteConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class OtherConfig {
    @PostConstruct
    public void init() {
        //检查有没有config文件夹，没有就创建一个
        final File file = new File("config");
        if (!file.exists()) {
            final boolean mkdir = file.mkdir();
            if (!mkdir)
                throw new RuntimeException("spring-config依赖包需要创建config文件夹,请确认是否有权限");
        }
    }

    @Bean
    public Map<String, Field> contentManager(){
        return new HashMap<>();
    }
}

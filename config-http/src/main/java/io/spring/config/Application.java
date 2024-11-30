package io.spring.config;

import io.spring.config.constant.DynamicConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
        System.out.println(DynamicConstant.test_float);
    }

}

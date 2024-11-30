package io.spring.config.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnityClass {
    boolean value() default true;
}

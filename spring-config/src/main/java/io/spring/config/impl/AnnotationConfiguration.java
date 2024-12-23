package io.spring.config.impl;

import io.spring.config.annotation.DynamicConfig;
import io.spring.config.annotation.UnityClass;
import io.spring.config.response.ResponseParam;
import io.spring.core.utils.ConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AnnotationConfiguration implements BeanPostProcessor {

    @Autowired
    private Environment environment;

    private static final Map<String, Field> contentManager = new HashMap<>();

    @Bean
    public Map<String, Field> contentManager() {
        return contentManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(UnityClass.class)) {
            boolean isRemote = Boolean.parseBoolean(environment.getProperty("spring.config.dynamic.enable-remote", "false"));
            Map<String, String> hashMap = new HashMap<>();
            if (isRemote) {
                 String servername = environment.getProperty("spring.config.dynamic.server-name");
                 if (servername ==null || Objects.equals(servername.trim(), ""))
                     throw new RuntimeException("spring.config.dynamic.server-name"+" must exists");

                 String baseUrl = environment.getProperty("spring.config.dynamic.base-url");
                 if (baseUrl==null)
                     throw new RuntimeException("spring.config.dynamic.base-url"+" must exists");
                ResponseParam param = new ResponseParam();
                final RestTemplate restTemplate = new RestTemplate();
                try {
                     param=restTemplate.getForObject(baseUrl + "/api/v1/getAllValue?serviceName=" + servername, ResponseParam.class);
                 } catch (Exception e) {
                     e.printStackTrace();
                     System.out.println("请确认"+baseUrl+"的正确性");
                 }
                 if (param==null)
                     throw new RuntimeException("请确认"+baseUrl+"的正确性");
                 if (param.getCode()!=0)
                     throw new RuntimeException(param.getMessage());
                 // 远程查询服务
                hashMap.putAll(param.getData());
             }
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()))
                    throw new RuntimeException("\"" + field.getName() + "\" 字段必须为public");
                if (!Modifier.isStatic(field.getModifiers()))
                    throw new RuntimeException("\"" + field.getName() + "\" 字段必须为static");
                if (field.isAnnotationPresent(DynamicConfig.class)) {
                    final String config_properties = field.getAnnotation(DynamicConfig.class).value();
                    if (Objects.equals("", config_properties.trim()))
                        throw new RuntimeException("\"" + field.getName() + "\" 字段key未配置");
                    if (!config_properties.contains(":")) {
                        //远程查询值，存在就直接用
                        if (hashMap.getOrDefault(config_properties, null)!=null){
                            ConfigUtils.convert(field, hashMap.get(config_properties));
                        } else {
                            final String environmentProperty = environment.getProperty(config_properties);
                            if (environmentProperty == null)
                                throw new RuntimeException("\"" + config_properties + "\" 值未配置");
                            ConfigUtils.convert(field, environmentProperty);
                        }
                    } else {
                        final String key = config_properties.substring(0, config_properties.indexOf(":"));
                        //远程查询值，存在就直接用
                        if (hashMap.getOrDefault(key, null)!=null){
                            ConfigUtils.convert(field, hashMap.get(key));
                        } else {
                            final String value = config_properties.substring(config_properties.indexOf(":") + 1);
                            final String property_value = environment.getProperty(key);
                            ConfigUtils.convert(field, property_value != null ? property_value : value);
                        }
                    }
                    contentManager.put(config_properties.split(":")[0], field);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}

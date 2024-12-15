package io.spring.config.impl;

import com.alibaba.fastjson2.JSON;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.spring.config.annotation.DynamicConfig;
import io.spring.config.annotation.UnityClass;
import io.spring.config.response.ResponseParam;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AnnotationConfiguration implements BeanPostProcessor {

    @Resource
    private Environment environment;

    @Resource
    private Map<String,Field> contentManager;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(UnityClass.class)) {
            boolean isRemote = bean.getClass().getAnnotation(UnityClass.class).value();
            Map<String, String> hashMap = new HashMap<>();
            if (isRemote) {
                 String servername = environment.getProperty("spring.config.dynamic.server-name");
                 if (servername ==null || Objects.equals(servername.trim(), ""))
                     throw new RuntimeException("spring.config.dynamic.server-name"+" must exists");

                 String baseUrl = environment.getProperty("spring.config.dynamic.base-url");
                 if (baseUrl==null)
                     throw new RuntimeException("spring.config.dynamic.base-url"+" must exists");
                ResponseParam param = new ResponseParam();
                 try {
                     param=restTemplate.getForObject(baseUrl + "/config/getAllValue?serviceName=" + servername, ResponseParam.class);
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
                if (field.isAnnotationPresent(DynamicConfig.class)) {
                    try {
                        final String config_properties = field.getAnnotation(DynamicConfig.class).value();
                        if (Objects.equals("", config_properties.trim()))
                            throw new RuntimeException("\"" + field.getName() + "\" 字段key未配置");
                        if (!config_properties.contains(":")) {
                            //远程查询值，存在就直接用
                            if (hashMap.getOrDefault(config_properties, null)!=null){
                                field.set(null, convert(field, hashMap.get(config_properties)));
                                continue;
                            }
                            final String environmentProperty = environment.getProperty(config_properties);
                            if (environmentProperty == null)
                                throw new RuntimeException("\"" + config_properties + "\" 值未配置");
                            field.set(null, convert(field, environmentProperty));
                        } else {
                            final String key = config_properties.substring(0, config_properties.indexOf(":"));
                            //远程查询值，存在就直接用
                            if (hashMap.getOrDefault(key, null)!=null){
                                field.set(null, convert(field, hashMap.get(key)));
                                continue;
                            }
                            final String value = config_properties.substring(config_properties.indexOf(":") + 1);
                            final String property_value = environment.getProperty(key);
                            field.set(null, convert(field, property_value != null ? property_value : value));
                        }
                        contentManager.put(config_properties.split(":")[0], field);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    //基础类型转换
    public static <T> T convert(Field field, String value ) {
        final Class<?> targetType = field.getType();
        try {
            if (targetType == String.class) {
                return (T) value;
            } else if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(value);
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(value);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(value);
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(value);
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(value);
            } else if (targetType == Character.class || targetType == char.class) {
                return (T) Character.valueOf(value.charAt(0));
            } else if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(value);
            } else if (targetType == List.class) {
                return (T)JSON.parseObject(value,List.class);
            } else if (targetType == Map.class) {
                return (T)JSON.parseObject(value,Map.class);
            }
            else {
                throw new IllegalArgumentException("Unsupported target type: " + targetType);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
    }
}

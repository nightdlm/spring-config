package io.spring.config.impl;

import io.spring.config.annotation.DynamicConfig;
import io.spring.config.annotation.UnityClass;
import io.spring.config.response.ResponseParam;
import io.spring.core.utils.ConfigUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Configuration
public class AnnotationConfiguration implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfiguration.class);
    private static ApplicationContext applicationContext;

    @Resource
    private Environment environment;

    @Resource
    private DynamicConfigProperties dynamicConfigProperties;

    private static final Map<String, Field> contentManager = new HashMap<>();

    @Bean
    public Map<String, Field> contentManager() {
        return contentManager;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(UnityClass.class)) {
            Map<String, String> hashMap = new HashMap<>();
            if (dynamicConfigProperties.getEnableRemote()) {
                 String servername = dynamicConfigProperties.getServerName();
                 if (servername ==null || Objects.equals(servername.trim(), ""))
                     throw new RuntimeException("spring.config.dynamic.server-name"+" must exists");

                 String baseUrl = dynamicConfigProperties.getBaseUrl();
                 if (baseUrl==null)
                     throw new RuntimeException("spring.config.dynamic.base-url"+" must exists");
                
                ResponseParam param = new ResponseParam();
                try {
                    // Use injected RestTemplate bean
                    RestTemplate restTemplate = getRestTemplate();
                    logger.info("Fetching remote config from: {}{}?serviceName={}", baseUrl, dynamicConfigProperties.getServerApi(), servername);
                    param = restTemplate.getForObject(baseUrl + dynamicConfigProperties.getServerApi() + "?serviceName=" + servername, ResponseParam.class);
                 } catch (Exception e) {
                     logger.error("Failed to fetch remote config from {}. Please check the URL.", baseUrl, e);
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
                            logger.debug("Set field {} with remote value", field.getName());
                        } else {
                            final String environmentProperty = environment.getProperty(config_properties);
                            if (environmentProperty == null)
                                throw new RuntimeException("\"" + config_properties + "\" 值未配置");
                            ConfigUtils.convert(field, environmentProperty);
                            logger.debug("Set field {} with local property", field.getName());
                        }
                    } else {
                        final String key = config_properties.substring(0, config_properties.indexOf(":"));
                        //远程查询值，存在就直接用
                        if (hashMap.getOrDefault(key, null)!=null){
                            ConfigUtils.convert(field, hashMap.get(key));
                            logger.debug("Set field {} with remote value (with default)", field.getName());
                        } else {
                            final String value = config_properties.substring(config_properties.indexOf(":") + 1);
                            final String property_value = environment.getProperty(key);
                            ConfigUtils.convert(field, property_value != null ? property_value : value);
                            logger.debug("Set field {} with default value", field.getName());
                        }
                    }
                    contentManager.put(config_properties.split(":")[0], field);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * Get RestTemplate bean from Spring context
     */
    private RestTemplate getRestTemplate() {
        // Try to get from Spring context, fallback to creating new instance
        try {
            if (applicationContext != null) {
                return applicationContext.getBean(RestTemplate.class);
            }
        } catch (Exception e) {
            logger.warn("Could not get RestTemplate bean, creating new instance");
        }
        return new RestTemplate();
    }

}

package io.spring.config.impl;

import lombok.Data;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

@ConfigurationProperties(prefix = "spring.config.dynamic")
@Data
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class DynamicConfigProperties {

    /**
     * 是否开启远程获取
     */
    private Boolean enableRemote=false;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 服务地址
     */
    private String baseUrl;

    private Boolean isSSL = false;


    private String serverApi = "/v1/getAllValue";
}

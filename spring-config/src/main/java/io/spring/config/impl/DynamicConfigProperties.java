package io.spring.config.impl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.config.dynamic")
@Data
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


    private String serverApi = "/v1/getAllValue";
}

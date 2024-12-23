package io.spring.config.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.config.dynamic")
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Boolean getEnableRemote() {
        return enableRemote;
    }

    public void setEnableRemote(Boolean enableRemote) {
        this.enableRemote = enableRemote;
    }
}

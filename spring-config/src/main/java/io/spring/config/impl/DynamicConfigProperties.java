package io.spring.config.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.config.dynamic")
public class DynamicConfigProperties {

    private String baseUrl = "http://127.0.0.1:23679";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

package io.spring.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.spring.config.domain.SysConfig;

public interface ISysConfigService extends IService<SysConfig> {
    
    /**
     * 获取配置值
     */
    String getConfigValue(String key);
    
    /**
     * 设置配置值
     */
    void setConfigValue(String key, String value);
    
    /**
     * 获取邮箱是否启用
     */
    boolean isEmailEnabled();
}

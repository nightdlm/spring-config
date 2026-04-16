package io.spring.config.service;

import io.spring.config.domain.ConfigHistory;
import io.spring.config.domain.SpringConfig;
import io.spring.config.request.UpdateConfig;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

public interface IConfigManageService {
    
    /**
     * 获取配置列表
     */
    List<SpringConfig> getConfigList(Integer serverId);
    
    /**
     * 删除配置
     */
    void deleteConfig(Integer id, HttpSession session);
    
    /**
     * 创建或更新配置
     */
    void saveOrUpdateConfig(UpdateConfig updateConfig, HttpSession session);
    
    /**
     * 发布配置
     */
    void publishConfig(Integer id);
    
    /**
     * 获取配置历史
     */
    List<ConfigHistory> getConfigHistory(Integer configId);
    
    /**
     * 回滚到指定版本
     */
    void rollbackToVersion(Integer historyId, HttpSession session);
    
    /**
     * 获取配置差异对比
     */
    Map<String, Object> getConfigDiff(Integer historyId);
}

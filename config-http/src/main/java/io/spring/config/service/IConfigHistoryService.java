package io.spring.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.spring.config.domain.ConfigHistory;

import java.util.List;

public interface IConfigHistoryService extends IService<ConfigHistory> {
    
    /**
     * 记录配置变更历史
     */
    void recordHistory(Integer configId, Integer serverId, String configKey, 
                      String oldValue, String newValue, Integer operatorId, 
                      String operatorName, String operationType, String remark);
    
    /**
     * 查询配置的历史版本
     */
    List<ConfigHistory> getConfigHistory(Integer configId);
    
    /**
     * 回滚到指定版本
     */
    void rollbackToVersion(Integer historyId, Integer operatorId, String operatorName);
}

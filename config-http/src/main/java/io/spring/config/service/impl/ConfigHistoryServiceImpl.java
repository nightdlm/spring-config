package io.spring.config.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.spring.config.domain.ConfigHistory;
import io.spring.config.domain.SpringConfig;
import io.spring.config.mapper.ConfigHistoryMapper;
import io.spring.config.service.IConfigHistoryService;
import io.spring.config.service.ISpringConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigHistoryServiceImpl extends ServiceImpl<ConfigHistoryMapper, ConfigHistory> implements IConfigHistoryService {
    
    @Autowired
    private ISpringConfigService springConfigService;

    @Override
    public void recordHistory(Integer configId, Integer serverId, String configKey, 
                             String oldValue, String newValue, Integer operatorId, 
                             String operatorName, String operationType, String remark) {
        ConfigHistory history = new ConfigHistory();
        history.setConfigId(configId);
        history.setServerId(serverId);
        history.setConfigKey(configKey);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setOperatorId(operatorId);
        history.setOperatorName(operatorName);
        history.setOperationType(operationType);
        history.setRemark(remark);
        this.save(history);
    }

    @Override
    public List<ConfigHistory> getConfigHistory(Integer configId) {
        return this.list(Wrappers.lambdaQuery(ConfigHistory.class)
                .eq(ConfigHistory::getConfigId, configId)
                .orderByDesc(ConfigHistory::getCreateTime));
    }

    @Override
    @Transactional
    public void rollbackToVersion(Integer historyId, Integer operatorId, String operatorName) {
        ConfigHistory history = this.getById(historyId);
        if (history == null) {
            throw new RuntimeException("历史记录不存在");
        }
        
        // 获取当前配置
        SpringConfig config = springConfigService.getById(history.getConfigId());
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }
        
        String oldValue = config.getValue();
        
        // 回滚到历史版本的值
        config.setValue(history.getOldValue());
        springConfigService.updateById(config);
        
        // 记录回滚操作
        recordHistory(
            config.getId(),
            config.getServerId(),
            config.getConfigKey(),
            oldValue,
            history.getOldValue(),
            operatorId,
            operatorName,
            "ROLLBACK",
            "回滚到版本ID: " + historyId
        );
    }
}

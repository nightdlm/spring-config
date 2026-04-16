package io.spring.config.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.ConfigHistory;
import io.spring.config.domain.ServerConfig;
import io.spring.config.domain.SpringConfig;
import io.spring.config.request.UpdateConfig;
import io.spring.config.service.*;
import io.spring.config.utils.DiffUtils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigManageServiceImpl implements IConfigManageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManageServiceImpl.class);
    
    @Autowired
    private ISpringConfigService springConfigService;
    
    @Autowired
    private IServerConfigService serverConfigService;
    
    @Autowired
    private IConfigHistoryService configHistoryService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private IAuthService authService;

    @Override
    public List<SpringConfig> getConfigList(Integer serverId) {
        return springConfigService.list(
                Wrappers.lambdaQuery(SpringConfig.class)
                        .eq(SpringConfig::getServerId, serverId)
                        .orderByDesc(SpringConfig::getCreateTime)
        );
    }

    @Override
    @Transactional
    public void deleteConfig(Integer id, HttpSession session) {
        // 验证登录
        authService.validateLogin(session);
        
        Integer operatorId = (Integer) session.getAttribute("currentUserId");
        String operatorName = (String) session.getAttribute("currentUserName");
        
        SpringConfig config = springConfigService.getById(id);
        if (config != null) {
            // 记录历史
            configHistoryService.recordHistory(
                    config.getId(),
                    config.getServerId(),
                    config.getConfigKey(),
                    config.getValue(),
                    null,
                    operatorId,
                    operatorName,
                    "DELETE",
                    "删除配置"
            );
            
            // 发送邮件通知
            emailService.sendConfigChangeNotification(
                    config.getConfigKey(),
                    config.getValue(),
                    null,
                    operatorName,
                    "DELETE"
            );
        }
        
        springConfigService.removeById(id);
        logger.info("Config deleted: id={}, operator={}", id, operatorName);
    }

    @Override
    @Transactional
    public void saveOrUpdateConfig(UpdateConfig updateConfig, HttpSession session) {
        // 验证登录
        authService.validateLogin(session);
        
        Integer operatorId = (Integer) session.getAttribute("currentUserId");
        String operatorName = (String) session.getAttribute("currentUserName");

        // 处理重复key
        if (updateConfig.getId() == null && springConfigService.exists(Wrappers.lambdaQuery(SpringConfig.class)
                .eq(SpringConfig::getServerId, updateConfig.getServerId())
                .eq(SpringConfig::getConfigKey, updateConfig.getKey()))) {
            throw new IllegalArgumentException(updateConfig.getKey() + "已存在");
        }

        SpringConfig oldConfig = null;
        if (updateConfig.getId() != null) {
            oldConfig = springConfigService.getById(updateConfig.getId());
        }

        SpringConfig config = new SpringConfig();
        config.setId(updateConfig.getId());
        config.setServerId(updateConfig.getServerId());
        config.setConfigKey(updateConfig.getKey());
        config.setValue(updateConfig.getValue());
        config.setUpdateTime(LocalDateTime.now());
        config.setDescription(updateConfig.getDesc());
        springConfigService.saveOrUpdate(config);
        
        // 记录历史
        String operationType = (updateConfig.getId() == null) ? "CREATE" : "UPDATE";
        String oldValue = (oldConfig != null) ? oldConfig.getValue() : null;
        configHistoryService.recordHistory(
                config.getId(),
                config.getServerId(),
                config.getConfigKey(),
                oldValue,
                config.getValue(),
                operatorId,
                operatorName,
                operationType,
                updateConfig.getDesc()
        );
        
        // 发送邮件通知
        emailService.sendConfigChangeNotification(
                config.getConfigKey(),
                oldValue,
                config.getValue(),
                operatorName,
                operationType
        );
        
        logger.info("Config updated: serverId={}, key={}, operator={}", 
                updateConfig.getServerId(), updateConfig.getKey(), operatorName);
    }

    @Override
    public void publishConfig(Integer id) {
        SpringConfig springConfig = springConfigService.getById(id);
        if (springConfig == null) {
            throw new RuntimeException("配置不存在");
        }
        
        HashMap<String, String> map = new HashMap<>();
        map.put(springConfig.getConfigKey(), springConfig.getValue());
        try {
            ServerConfig serverConfig = serverConfigService.getById(springConfig.getServerId());
            if (serverConfig == null) {
                throw new RuntimeException("服务器配置不存在");
            }
            String serverName = serverConfig.getServerName();
            String jsonMessage = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
            redisTemplate.convertAndSend(serverName, jsonMessage);
            logger.info("Published config to Redis channel {}: {}", serverName, jsonMessage);
        } catch (Exception e) {
            logger.error("Failed to publish config to Redis", e);
            throw new RuntimeException("发布配置失败", e);
        }
    }

    @Override
    public List<ConfigHistory> getConfigHistory(Integer configId) {
        return configHistoryService.getConfigHistory(configId);
    }

    @Override
    @Transactional
    public void rollbackToVersion(Integer historyId, HttpSession session) {
        // 验证登录
        authService.validateLogin(session);
        
        Integer operatorId = (Integer) session.getAttribute("currentUserId");
        String operatorName = (String) session.getAttribute("currentUserName");
        
        configHistoryService.rollbackToVersion(historyId, operatorId, operatorName);
        logger.info("Config rolled back: historyId={}, operator={}", historyId, operatorName);
    }

    @Override
    public Map<String, Object> getConfigDiff(Integer historyId) {
        ConfigHistory history = configHistoryService.getById(historyId);
        if (history == null) {
            throw new RuntimeException("历史记录不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("history", history);
        result.put("diff", DiffUtils.diff(history.getOldValue(), history.getNewValue()));
        result.put("htmlDiff", DiffUtils.generateHtmlDiff(history.getOldValue(), history.getNewValue()));
        
        return result;
    }
}

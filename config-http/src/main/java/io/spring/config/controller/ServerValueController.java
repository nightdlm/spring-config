package io.spring.config.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.config.domain.SpringConfig;
import io.spring.config.request.UpdateConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IServerConfigService;
import io.spring.config.service.ISpringConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@Valid
@CrossOrigin
public class ServerValueController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServerValueController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ISpringConfigService iSpringConfigService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private IServerConfigService iServerConfigService;

    @PostMapping("/getList/{id}")
    public ApiResponse<List<SpringConfig>> getList(@PathVariable Integer id) {
        return ApiResponse.of(iSpringConfigService.list(
                Wrappers.lambdaQuery(SpringConfig.class)
                .eq(SpringConfig::getServerId,id)
                .orderByDesc(SpringConfig::getCreateTime)
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        iSpringConfigService.removeById(id);
        return ApiResponse.success();
    }

    @PostMapping("/updateInfo")
    public ApiResponse<Void> create(@RequestBody UpdateConfig updateConfig){

        //处理重复key
        if (updateConfig.getId()==null && iSpringConfigService.exists(Wrappers.lambdaQuery(SpringConfig.class)
                .eq(SpringConfig::getServerId,updateConfig.getServerId())
                .eq(SpringConfig::getConfigKey,updateConfig.getKey()))) {
            throw new IllegalArgumentException(updateConfig.getKey()+"已存在");
        }

        SpringConfig config = new SpringConfig();
        config.setId(updateConfig.getId());
        config.setServerId(updateConfig.getServerId());
        config.setConfigKey(updateConfig.getKey());
        config.setValue(updateConfig.getValue());
        config.setUpdateTime(LocalDateTime.now());
        config.setDescription(updateConfig.getDesc());
        iSpringConfigService.saveOrUpdate(config);
        logger.info("Config updated: serverId={}, key={}", updateConfig.getServerId(), updateConfig.getKey());
        return ApiResponse.success();
    }

    //发布配置
    @GetMapping("/publish/{id}")
    public ApiResponse<Void> publishValue(@PathVariable Integer id){
        SpringConfig springConfig = iSpringConfigService.getById(id);
        HashMap<String, String> map = new HashMap<>();
        map.put(springConfig.getConfigKey(), springConfig.getValue());
        try {
            String serverName = iServerConfigService.getById(springConfig.getServerId()).getServerName();
            String jsonMessage = objectMapper.writeValueAsString(map);
            redisTemplate.convertAndSend(serverName, jsonMessage);
            logger.info("Published config to Redis channel {}: {}", serverName, jsonMessage);
        } catch (Exception e) {
            logger.error("Failed to publish config to Redis", e);
            throw new RuntimeException("发布配置失败", e);
        }
        return ApiResponse.success();
    }

}

package io.spring.config.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.SpringConfig;
import io.spring.config.request.UpdateConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IServerConfigService;
import io.spring.config.service.ISpringConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/value")
@Valid
public class ServerValueController {
    @Autowired
    private ISpringConfigService iSpringConfigService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private IServerConfigService iServerConfigService;

    @PostMapping("/getList")
    public ApiResponse<List<SpringConfig>> getList(Integer id) {
        return ApiResponse.of(iSpringConfigService.list(
                Wrappers.lambdaQuery(SpringConfig.class)
                .eq(SpringConfig::getServerId,id)
        ));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody UpdateConfig updateConfig) {
        SpringConfig config = new SpringConfig();
        config.setId(updateConfig.getId());
        config.setConfigKey(updateConfig.getKey());
        config.setValue(updateConfig.getValue());
        config.setUpdateTime(LocalDateTime.now());
        config.setCreateTime(LocalDateTime.now());
        config.setDescription(updateConfig.getDesc());
        boolean b = iSpringConfigService.updateById(config);
        if (b){
            SpringConfig service = iSpringConfigService.getById(updateConfig.getId());
            HashMap<String, String> map = new HashMap<>();
            map.put(updateConfig.getKey(), updateConfig.getValue());
            redisTemplate.convertAndSend(iServerConfigService.getById(service.getServerId()).getServerName(),JSON.toJSONString(map));
        }
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(Integer id) {
        iSpringConfigService.removeById(id);
        return ApiResponse.success();
    }

    @PostMapping("/create")
    public ApiResponse<Void> create(@RequestBody UpdateConfig updateConfig){
        SpringConfig config = new SpringConfig();
        config.setServerId(updateConfig.getId());
        config.setConfigKey(updateConfig.getKey());
        config.setValue(updateConfig.getValue());
        config.setUpdateTime(LocalDateTime.now());
        config.setCreateTime(LocalDateTime.now());
        config.setDescription(updateConfig.getDesc());
        boolean save = iSpringConfigService.save(config);
        if (save){
            HashMap<String, String> map = new HashMap<>();
            map.put(updateConfig.getKey(), updateConfig.getValue());
            redisTemplate.convertAndSend(iServerConfigService.getById(updateConfig.getId()).getServerName(), JSON.toJSONString(map));
        }
        return ApiResponse.success();
    }

}

package io.spring.config.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.ServerConfig;
import io.spring.config.domain.SpringConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.response.ResponseParam;
import io.spring.config.service.IServerConfigService;
import io.spring.config.service.ISpringConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1")
@RestController
public class DynamicQueryController {

    @Autowired
    private ISpringConfigService springConfigService;

    @Autowired
    private IServerConfigService serverConfigService;

    @GetMapping("/getAllValue")
    public ApiResponse<Map<String,String>> getAllValue(String serviceName){
        ServerConfig serverConfig = serverConfigService.getOne(Wrappers.lambdaQuery(ServerConfig.class).eq(ServerConfig::getServerName, serviceName));
        if (serverConfig==null)
            throw new RuntimeException("服务不存在，请先创建，详情访问https://github.com/nightdlm/spring-config");
        HashMap<String, String> hashMap = new HashMap<>();
        List<SpringConfig> list = springConfigService
                .list(Wrappers.lambdaQuery(SpringConfig.class).eq(SpringConfig::getServerId, serverConfig.getId()));
        list.forEach(springConfig -> hashMap.put(springConfig.getConfigKey(),springConfig.getValue()));
        return ApiResponse.of(hashMap);
    }

}

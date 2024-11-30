package io.spring.config.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.ServerConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/name")
public class ServerNameController {

    @Autowired
    private IServerConfigService iServerConfigService;

    @PostMapping("/createServer")
    public ApiResponse<Void> createServer(String serverName) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerName(serverName);
        iServerConfigService.save(serverConfig);
        return ApiResponse.success();
    }

    @PostMapping("/updateServerName")
    public ApiResponse<Void> updateServerName(Integer id,String newServerName) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerName(newServerName);
        serverConfig.setId(id);
        iServerConfigService.updateById(serverConfig);
        return ApiResponse.success();
    }

    @PostMapping("/deleteServer")
    public ApiResponse<Void> deleteServer(Integer id) {
        iServerConfigService.removeById(id);
        return ApiResponse.success();
    }

    @PostMapping("/getServer")
    public ApiResponse<List<String>> getServer(String serverName) {
        List<String> list = iServerConfigService.listObjs(
                Wrappers.lambdaQuery(ServerConfig.class)
                        .select(ServerConfig::getServerName)
                        .like(serverName != null && !"".equals(serverName.trim()), ServerConfig::getServerName, serverName)
        );
        return ApiResponse.of(list);
    }
}

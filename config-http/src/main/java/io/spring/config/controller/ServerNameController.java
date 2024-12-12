package io.spring.config.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.ServerConfig;
import io.spring.config.domain.SpringConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IServerConfigService;
import io.spring.config.service.ISpringConfigService;
import io.spring.core.param.ServerValueParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ServerNameController {

    @Autowired
    private IServerConfigService iServerConfigService;

    @Autowired
    private ISpringConfigService iSpringConfigService;

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
        if (iSpringConfigService.exists(Wrappers.lambdaQuery(SpringConfig.class).eq(SpringConfig::getServerId,id)))
            throw new RuntimeException("不可删除存在配置的服务");
        if (!iServerConfigService.removeById(id)) throw new RuntimeException("删除失败");
        return ApiResponse.success();
    }

    @PostMapping("/getServerList")
    public ApiResponse<List<ServerConfig>> getServer() {
        List<ServerConfig> list = iServerConfigService.list();
        return ApiResponse.of(list);
    }
}

package io.spring.config.controller.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.ServerConfig;
import io.spring.config.domain.SpringConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IServerConfigService;
import io.spring.config.service.ISpringConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("server")
public class ServerNameController {

    private static final Logger logger = LoggerFactory.getLogger(ServerNameController.class);

    @Autowired
    private IServerConfigService iServerConfigService;

    @Autowired
    private ISpringConfigService iSpringConfigService;

    @PostMapping("/createServer")
    public ApiResponse<Void> createServer(String serverName) {
        if (iServerConfigService.exists(Wrappers.lambdaQuery(ServerConfig.class).eq(ServerConfig::getServerName,serverName)))
            throw new RuntimeException("存在同名服务");
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerName(serverName);
        iServerConfigService.save(serverConfig);
        logger.info("Server created: {}", serverName);
        return ApiResponse.success();
    }

    @PostMapping("/updateServerName")
    public ApiResponse<Void> updateServerName(Integer id,String newServerName) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerName(newServerName);
        serverConfig.setId(id);
        iServerConfigService.updateById(serverConfig);
        logger.info("Server name updated: id={}, newName={}", id, newServerName);
        return ApiResponse.success();
    }

    @PostMapping("/deleteServer")
    public ApiResponse<Void> deleteServer(Integer id) {
        if (iSpringConfigService.exists(Wrappers.lambdaQuery(SpringConfig.class).eq(SpringConfig::getServerId,id)))
            throw new RuntimeException("不可删除存在配置的服务");
        if (!iServerConfigService.removeById(id)) throw new RuntimeException("删除失败");
        logger.info("Server deleted: id={}", id);
        return ApiResponse.success();
    }

    @PostMapping("/getServerList")
    public ApiResponse<List<ServerConfig>> getServer() {
        List<ServerConfig> list = iServerConfigService.list();
        return ApiResponse.of(list);
    }
}

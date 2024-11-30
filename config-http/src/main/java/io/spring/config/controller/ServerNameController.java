package io.spring.config.controller;

import io.spring.config.response.ApiResponse;
import io.spring.config.service.ServerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping("/api/name")
@Valid
public class ServerNameController {

    @Autowired
    private ServerManager serverManager;

    @PostMapping("/createServer")
    public ApiResponse<Void> createServer(String serverName) {
        serverManager.createServer(serverName);
        return ApiResponse.success();
    }

    @PostMapping("/updateServerName")
    public ApiResponse<Void> updateServerName(String serverName, String newServerName) {
        serverManager.updateServer(serverName, newServerName);
        return ApiResponse.success();
    }

    @PostMapping("/deleteServer")
    public ApiResponse<Void> deleteServer(String serverName) {
        serverManager.deleteServer(serverName);
        return ApiResponse.success();
    }

    @PostMapping("/getServer")
    public ApiResponse<List<String>> getServer(String serverName) {
        return ApiResponse.of(serverManager.getAllServer(serverName));
    }
}

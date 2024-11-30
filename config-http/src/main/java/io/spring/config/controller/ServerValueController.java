package io.spring.config.controller;

import io.spring.config.request.UpdateConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.ServerValueService;
import io.spring.core.param.ServerValueParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/value")
@Valid
public class ServerValueController {
    @Autowired
    private ServerValueService serverValueService;

    @PostMapping("/getList")
    public ApiResponse<List<ServerValueParam>> getList(@RequestBody String serverName) {
        return ApiResponse.of(serverValueService.getServerValue(serverName));
    }

    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody UpdateConfig updateConfig) {
        serverValueService.updateServerValue(updateConfig.getServerName(),updateConfig.getKey(),updateConfig.getValue());
        return ApiResponse.success();
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody UpdateConfig updateConfig) {
        serverValueService.deleteServerValue(updateConfig.getServerName(),updateConfig.getKey());
        return ApiResponse.success();
    }

}

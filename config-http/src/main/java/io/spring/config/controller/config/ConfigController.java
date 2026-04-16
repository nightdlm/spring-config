package io.spring.config.controller.config;

import io.spring.config.domain.ConfigHistory;
import io.spring.config.domain.SpringConfig;
import io.spring.config.request.UpdateConfig;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IConfigManageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {
    
    @Autowired
    private IConfigManageService configManageService;

    /**
     * 获取配置列表
     */
    @PostMapping("/list/{id}")
    public ApiResponse<List<SpringConfig>> getList(@PathVariable Integer id) {
        return ApiResponse.of(configManageService.getConfigList(id));
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id, HttpSession session) {
        configManageService.deleteConfig(id, session);
        return ApiResponse.success();
    }

    /**
     * 创建或更新配置
     */
    @PostMapping("/update")
    public ApiResponse<Void> create(@Valid @RequestBody UpdateConfig updateConfig, HttpSession session){
        configManageService.saveOrUpdateConfig(updateConfig, session);
        return ApiResponse.success();
    }

    /**
     * 发布配置
     */
    @GetMapping("/publish/{id}")
    public ApiResponse<Void> publishValue(@PathVariable Integer id){
        configManageService.publishConfig(id);
        return ApiResponse.success();
    }

    /**
     * 获取配置历史
     */
    @GetMapping("/history/{configId}")
    public ApiResponse<List<ConfigHistory>> getHistory(@PathVariable Integer configId) {
        return ApiResponse.of(configManageService.getConfigHistory(configId));
    }

    /**
     * 回滚到指定版本
     */
    @PostMapping("/rollback/{historyId}")
    public ApiResponse<Void> rollback(@PathVariable Integer historyId, HttpSession session) {
        configManageService.rollbackToVersion(historyId, session);
        return ApiResponse.success();
    }

    /**
     * 获取配置差异对比
     */
    @GetMapping("/diff/{historyId}")
    public ApiResponse<Map<String, Object>> getDiff(@PathVariable Integer historyId) {
        return ApiResponse.of(configManageService.getConfigDiff(historyId));
    }
}

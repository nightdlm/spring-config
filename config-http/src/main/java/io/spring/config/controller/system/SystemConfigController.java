package io.spring.config.controller.system;

import io.spring.config.domain.SysConfig;
import io.spring.config.request.SystemConfigRequest;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IAuthService;
import io.spring.config.service.ISysConfigService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SystemConfigController {
    
    @Autowired
    private ISysConfigService sysConfigService;
    
    @Autowired
    private IAuthService authService;
    
    /**
     * 获取所有系统配置
     */
    @GetMapping("/configs")
    public ApiResponse<List<SysConfig>> getAllConfigs(HttpSession session) {
        // 验证登录
        authService.validateLogin(session);
        
        return ApiResponse.of(sysConfigService.list());
    }
    
    /**
     * 更新系统配置
     */
    @PostMapping("/config")
    public ApiResponse<Void> updateConfig(@Valid @RequestBody SystemConfigRequest configData, HttpSession session) {
        // 验证管理员权限
        authService.validateAdmin(session);
        
        sysConfigService.setConfigValue(configData.getKey(), configData.getValue());
        return ApiResponse.success();
    }
    
    /**
     * 测试邮件配置
     */
    @PostMapping("/test-email")
    public ApiResponse<Void> testEmail(HttpSession session) {
        // 验证管理员权限
        authService.validateAdmin(session);
        
        // TODO: 实现邮件测试功能
        
        return ApiResponse.success();
    }
}

package io.spring.config.controller.system;

import io.spring.config.request.LoginRequest;
import io.spring.config.response.ApiResponse;
import io.spring.config.response.LoginResponse;
import io.spring.config.service.IAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private IAuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                             HttpSession session) {
        LoginResponse response = authService.login(
                loginRequest.getUsername(), 
                loginRequest.getPassword(), 
                session
        );
        return ApiResponse.of(response);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        authService.logout(session);
        return ApiResponse.success();
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ApiResponse<LoginResponse> getCurrentUser(HttpSession session) {
        LoginResponse response = authService.getCurrentUser(session);
        return ApiResponse.of(response);
    }
}

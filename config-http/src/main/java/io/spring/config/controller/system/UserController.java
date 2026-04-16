package io.spring.config.controller.system;

import io.spring.config.request.CreateUserRequest;
import io.spring.config.request.UpdatePasswordRequest;
import io.spring.config.response.ApiResponse;
import io.spring.config.response.UserResponse;
import io.spring.config.service.IAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private IAuthService authService;
    
    /**
     * 创建用户（仅管理员）
     */
    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request, 
                                                 HttpSession session) {
        UserResponse response = authService.createUser(request, session);
        return ApiResponse.of(response);
    }
    
    /**
     * 获取用户列表（仅管理员）
     */
    @GetMapping("/list")
    public ApiResponse<List<UserResponse>> getUserList(HttpSession session) {
        List<UserResponse> users = authService.getUserList(session);
        return ApiResponse.of(users);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request, 
                                             HttpSession session) {
        authService.updatePassword(request, session);
        return ApiResponse.success();
    }
    
    /**
     * 重置用户密码（仅管理员）
     */
    @PostMapping("/reset-password/{userId}")
    public ApiResponse<Void> resetPassword(@PathVariable Integer userId, 
                                           @RequestParam String newPassword,
                                           HttpSession session) {
        authService.resetUserPassword(userId, newPassword, session);
        return ApiResponse.success();
    }
    
    /**
     * 删除用户（仅管理员）
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer userId, HttpSession session) {
        authService.deleteUser(userId, session);
        return ApiResponse.success();
    }
}

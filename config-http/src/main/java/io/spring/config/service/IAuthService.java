package io.spring.config.service;

import io.spring.config.domain.SysUser;
import io.spring.config.request.CreateUserRequest;
import io.spring.config.request.UpdatePasswordRequest;
import io.spring.config.response.LoginResponse;
import io.spring.config.response.UserResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IAuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(String username, String password, HttpSession session);
    
    /**
     * 用户登出
     */
    void logout(HttpSession session);
    
    /**
     * 获取当前用户信息
     */
    LoginResponse getCurrentUser(HttpSession session);
    
    /**
     * 验证用户是否已登录
     */
    SysUser validateLogin(HttpSession session);
    
    /**
     * 验证是否为管理员
     */
    void validateAdmin(HttpSession session);
    
    /**
     * 创建用户（仅管理员）
     */
    UserResponse createUser(CreateUserRequest request, HttpSession session);
    
    /**
     * 获取用户列表（仅管理员）
     */
    List<UserResponse> getUserList(HttpSession session);
    
    /**
     * 修改密码
     */
    void updatePassword(UpdatePasswordRequest request, HttpSession session);
    
    /**
     * 管理员重置用户密码
     */
    void resetUserPassword(Integer userId, String newPassword, HttpSession session);
    
    /**
     * 删除用户（仅管理员）
     */
    void deleteUser(Integer userId, HttpSession session);
}

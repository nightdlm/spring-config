package io.spring.config.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.spring.config.domain.SysRole;
import io.spring.config.domain.SysUser;
import io.spring.config.request.CreateUserRequest;
import io.spring.config.request.UpdatePasswordRequest;
import io.spring.config.response.LoginResponse;
import io.spring.config.response.UserResponse;
import io.spring.config.service.IAuthService;
import io.spring.config.service.ISysRoleService;
import io.spring.config.service.ISysUserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private ISysUserService sysUserService;
    
    @Autowired
    private ISysRoleService sysRoleService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(String username, String password, HttpSession session) {
        SysUser user = sysUserService.login(username, password);
        
        // 获取用户角色
        SysRole role = sysRoleService.getRoleByUserId(user.getId());
        
        // 将用户信息存入session
        session.setAttribute("currentUser", user);
        session.setAttribute("currentUserId", user.getId());
        session.setAttribute("currentUserName", user.getNickname() != null ? user.getNickname() : user.getUsername());
        session.setAttribute("currentUserRole", role != null ? role.getRoleCode() : "USER");
        session.setAttribute("currentRoleId", role != null ? role.getId() : null);
        
        logger.info("User logged in: {}", username);
        
        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(role != null ? role.getRoleCode() : "USER")
                .roleId(role != null ? role.getId() : null)
                .roleCode(role != null ? role.getRoleCode() : null)
                .build();
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
        logger.info("User logged out");
    }

    @Override
    public LoginResponse getCurrentUser(HttpSession session) {
        SysUser user = validateLogin(session);
        SysRole role = sysRoleService.getRoleByUserId(user.getId());
        
        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(role != null ? role.getRoleCode() : "USER")
                .roleId(role != null ? role.getId() : null)
                .roleCode(role != null ? role.getRoleCode() : null)
                .build();
    }

    @Override
    public SysUser validateLogin(HttpSession session) {
        Object userObj = session.getAttribute("currentUser");
        if (userObj == null) {
            throw new RuntimeException("请先登录");
        }
        return (SysUser) userObj;
    }

    @Override
    public void validateAdmin(HttpSession session) {
        SysUser user = validateLogin(session);
        SysRole role = sysRoleService.getRoleByUserId(user.getId());
        
        if (role == null || !"ADMIN".equals(role.getRoleCode())) {
            throw new RuntimeException("无权限操作");
        }
    }
    
    /**
     * 验证并返回管理员用户
     */
    private SysUser validateAndGetAdmin(HttpSession session) {
        validateAdmin(session);
        return validateLogin(session);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request, HttpSession session) {
        // 验证管理员权限
        validateAdmin(session);
        
        // 检查用户名是否已存在
        if (sysUserService.getByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setRoleId(request.getRoleId());
        user.setStatus(request.getStatus());
        
        sysUserService.save(user);
        
        logger.info("User created: {} by admin", request.getUsername());
        
        // 获取角色信息
        SysRole role = sysRoleService.getById(request.getRoleId());
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(role != null ? role.getRoleCode() : "USER")
                .roleId(user.getRoleId())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }

    @Override
    public List<UserResponse> getUserList(HttpSession session) {
        // 验证管理员权限
        validateAdmin(session);
        
        List<SysUser> users = sysUserService.list();
        
        return users.stream()
                .map(user -> {
                    SysRole role = sysRoleService.getById(user.getRoleId());
                    return UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .role(role != null ? role.getRoleCode() : "USER")
                            .roleId(user.getRoleId())
                            .status(user.getStatus())
                            .createTime(user.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request, HttpSession session) {
        SysUser currentUser = validateLogin(session);
        Integer currentUserId = (Integer) session.getAttribute("currentUserId");
        
        SysUser user = sysUserService.getById(currentUserId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        sysUserService.updateById(user);
        
        logger.info("Password updated for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void resetUserPassword(Integer userId, String newPassword, HttpSession session) {
        // 验证管理员权限并获取管理员信息
        SysUser admin = validateAndGetAdmin(session);
        
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果是内置管理员账户，只有内置管理员可以修改
        if ("admin".equals(user.getUsername()) && !"admin".equals(admin.getUsername())) {
            throw new RuntimeException("无权限修改内置管理员密码");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserService.updateById(user);
        
        logger.info("Password reset for user: {} by admin: {}", user.getUsername(), admin.getUsername());
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId, HttpSession session) {
        // 验证管理员权限并获取管理员信息
        SysUser admin = validateAndGetAdmin(session);
        
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不允许删除内置管理员
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("不能删除内置管理员账户");
        }
        
        // 不允许删除自己
        if (userId.equals(admin.getId())) {
            throw new RuntimeException("不能删除自己的账户");
        }
        
        sysUserService.removeById(userId);
        
        logger.info("User deleted: {} by admin: {}", user.getUsername(), admin.getUsername());
    }
}

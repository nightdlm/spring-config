package io.spring.config.controller.system;

import io.spring.config.domain.SysPermission;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IAuthService;
import io.spring.config.service.ISysPermissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    
    @Autowired
    private ISysPermissionService sysPermissionService;
    
    @Autowired
    private IAuthService authService;
    
    /**
     * 获取当前用户的菜单权限
     */
    @GetMapping("/menus")
    public ApiResponse<List<SysPermission>> getUserMenus(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("currentUserId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        List<SysPermission> menus = sysPermissionService.getPermissionsByUserId(userId);
        // 只返回菜单类型的权限
        List<SysPermission> menuList = menus.stream()
                .filter(p -> "MENU".equals(p.getPermType()))
                .toList();
        
        return ApiResponse.of(menuList);
    }
    
    /**
     * 获取所有权限列表（仅管理员）
     */
    @GetMapping("/list")
    public ApiResponse<List<SysPermission>> getAllPermissions(HttpSession session) {
        authService.validateAdmin(session);
        return ApiResponse.of(sysPermissionService.list());
    }
    
    /**
     * 获取API权限列表（仅管理员）
     */
    @GetMapping("/api-list")
    public ApiResponse<List<SysPermission>> getApiPermissions(HttpSession session) {
        authService.validateAdmin(session);
        return ApiResponse.of(sysPermissionService.getApiPermissions());
    }
    
    /**
     * 创建权限（仅管理员）
     */
    @PostMapping("/create")
    public ApiResponse<Void> createPermission(@RequestBody SysPermission permission, HttpSession session) {
        authService.validateAdmin(session);
        sysPermissionService.save(permission);
        return ApiResponse.success();
    }
    
    /**
     * 更新权限（仅管理员）
     */
    @PostMapping("/update")
    public ApiResponse<Void> updatePermission(@RequestBody SysPermission permission, HttpSession session) {
        authService.validateAdmin(session);
        sysPermissionService.updateById(permission);
        return ApiResponse.success();
    }
    
    /**
     * 删除权限（仅管理员）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Integer id, HttpSession session) {
        authService.validateAdmin(session);
        sysPermissionService.removeById(id);
        return ApiResponse.success();
    }
}

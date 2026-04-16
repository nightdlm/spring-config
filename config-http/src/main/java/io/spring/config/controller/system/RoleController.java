package io.spring.config.controller.system;

import io.spring.config.domain.SysRole;
import io.spring.config.response.ApiResponse;
import io.spring.config.service.IAuthService;
import io.spring.config.service.ISysRoleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    
    @Autowired
    private ISysRoleService sysRoleService;
    
    @Autowired
    private IAuthService authService;
    
    /**
     * 获取所有角色
     */
    @GetMapping("/list")
    public ApiResponse<List<SysRole>> getAllRoles(HttpSession session) {
        authService.validateLogin(session);
        return ApiResponse.of(sysRoleService.list());
    }
    
    /**
     * 创建角色（仅管理员）
     */
    @PostMapping("/create")
    public ApiResponse<Void> createRole(@RequestBody SysRole role, HttpSession session) {
        authService.validateAdmin(session);
        sysRoleService.save(role);
        return ApiResponse.success();
    }
    
    /**
     * 更新角色（仅管理员）
     */
    @PostMapping("/update")
    public ApiResponse<Void> updateRole(@RequestBody SysRole role, HttpSession session) {
        authService.validateAdmin(session);
        sysRoleService.updateById(role);
        return ApiResponse.success();
    }
    
    /**
     * 删除角色（仅管理员）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Integer id, HttpSession session) {
        authService.validateAdmin(session);
        sysRoleService.removeById(id);
        return ApiResponse.success();
    }
    
    /**
     * 为角色分配权限（仅管理员）
     */
    @PostMapping("/assign-permissions")
    public ApiResponse<Void> assignPermissions(@RequestParam Integer roleId, 
                                               @RequestBody List<Integer> permissionIds,
                                               HttpSession session) {
        authService.validateAdmin(session);
        sysRoleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.success();
    }
    
    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/permissions/{roleId}")
    public ApiResponse<List<Integer>> getRolePermissions(@PathVariable Integer roleId, HttpSession session) {
        authService.validateLogin(session);
        List<Integer> permissionIds = sysRoleService.getPermissionIdsByRoleId(roleId);
        return ApiResponse.of(permissionIds);
    }
}

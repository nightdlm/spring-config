package io.spring.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.spring.config.domain.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {
    
    /**
     * 根据用户ID获取角色
     */
    SysRole getRoleByUserId(Integer userId);
    
    /**
     * 获取用户的权限编码列表
     */
    List<String> getUserPermissions(Integer userId);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Integer userId, String permissionCode);
    
    /**
     * 为角色分配权限
     */
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
}

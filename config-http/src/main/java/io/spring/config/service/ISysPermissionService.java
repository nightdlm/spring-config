package io.spring.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.spring.config.domain.SysPermission;

import java.util.List;

public interface ISysPermissionService extends IService<SysPermission> {
    
    /**
     * 根据用户ID获取权限列表
     */
    List<SysPermission> getPermissionsByUserId(Integer userId);
    
    /**
     * 获取菜单权限（树形结构）
     */
    List<SysPermission> getMenuTree();
    
    /**
     * 获取所有API权限
     */
    List<SysPermission> getApiPermissions();
}

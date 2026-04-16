package io.spring.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.spring.config.domain.SysRole;
import io.spring.config.mapper.SysRoleMapper;
import io.spring.config.mapper.SysRolePermissionMapper;
import io.spring.config.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public SysRole getRoleByUserId(Integer userId) {
        return sysRoleMapper.getRoleByUserId(userId);
    }

    @Override
    public List<String> getUserPermissions(Integer userId) {
        SysRole role = getRoleByUserId(userId);
        if (role == null) {
            return List.of();
        }
        return sysRoleMapper.getPermissionCodesByRoleId(role.getId());
    }

    @Override
    public boolean hasPermission(Integer userId, String permissionCode) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode);
    }

    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        // 先删除原有权限
        sysRolePermissionMapper.deleteByRoleId(roleId);
        
        // 再添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            sysRolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }
}

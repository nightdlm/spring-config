package io.spring.config.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.spring.config.domain.SysPermission;
import io.spring.config.mapper.SysPermissionMapper;
import io.spring.config.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {
    
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> getPermissionsByUserId(Integer userId) {
        return sysPermissionMapper.getPermissionsByUserId(userId);
    }

    @Override
    public List<SysPermission> getMenuTree() {
        List<SysPermission> menus = sysPermissionMapper.getMenuPermissions();
        
        // 构建树形结构
        return buildMenuTree(menus, 0);
    }

    @Override
    public List<SysPermission> getApiPermissions() {
        return list(Wrappers.<SysPermission>lambdaQuery()
                .eq(SysPermission::getPermType, "API")
                .eq(SysPermission::getStatus, 1)
                .eq(SysPermission::getIsDeleted, 0)
                .orderByAsc(SysPermission::getSortOrder));
    }
    
    /**
     * 构建菜单树
     */
    private List<SysPermission> buildMenuTree(List<SysPermission> allMenus, Integer parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> {
                    // 递归查找子菜单
                    List<SysPermission> children = buildMenuTree(allMenus, menu.getId());
                    // 这里可以设置children到menu对象，如果需要的话
                })
                .collect(Collectors.toList());
    }
}

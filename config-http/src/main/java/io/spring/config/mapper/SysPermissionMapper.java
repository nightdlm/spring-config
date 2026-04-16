package io.spring.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.spring.config.domain.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    
    /**
     * 根据用户ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user u ON rp.role_id = u.role_id " +
            "WHERE u.id = #{userId} AND p.is_deleted = 0 AND p.status = 1 " +
            "ORDER BY p.sort_order ASC")
    List<SysPermission> getPermissionsByUserId(Integer userId);
    
    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.is_deleted = 0 AND p.status = 1 " +
            "ORDER BY p.sort_order ASC")
    List<SysPermission> getPermissionsByRoleId(Integer roleId);
    
    /**
     * 查询菜单权限（树形结构）
     */
    @Select("SELECT * FROM sys_permission " +
            "WHERE perm_type = 'MENU' AND is_deleted = 0 AND status = 1 " +
            "ORDER BY sort_order ASC")
    List<SysPermission> getMenuPermissions();
}

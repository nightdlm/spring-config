package io.spring.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.spring.config.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    
    /**
     * 根据用户ID查询角色
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user u ON r.id = u.role_id " +
            "WHERE u.id = #{userId} AND r.is_deleted = 0")
    SysRole getRoleByUserId(Integer userId);
    
    /**
     * 查询角色的权限编码列表
     */
    @Select("SELECT p.perm_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.is_deleted = 0 AND p.status = 1")
    List<String> getPermissionCodesByRoleId(Integer roleId);
}

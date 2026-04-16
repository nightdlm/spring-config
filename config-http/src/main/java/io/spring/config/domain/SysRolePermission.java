package io.spring.config.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer roleId;
    
    private Integer permissionId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

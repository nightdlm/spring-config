package io.spring.config.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("config_history")
public class ConfigHistory {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer configId;
    
    private Integer serverId;
    
    private String configKey;
    
    private String oldValue;
    
    private String newValue;
    
    private Integer operatorId;
    
    private String operatorName;
    
    private String operationType;
    
    private Integer rollbackVersionId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private String remark;
    
    @TableLogic
    private Integer isDeleted;
}

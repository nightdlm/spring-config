package io.spring.config.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String nickname;
    
    private String email;
    
    @NotNull(message = "角色ID不能为空")
    private Integer roleId;
    
    @NotNull(message = "状态不能为空")
    private Integer status;
}

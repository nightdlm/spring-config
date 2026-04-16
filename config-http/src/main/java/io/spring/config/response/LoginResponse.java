package io.spring.config.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Integer id;
    private String username;
    private String nickname;
    private String role;        // 角色编码（兼容旧版）
    private Integer roleId;     // 角色ID
    private String roleCode;    // 角色编码
}

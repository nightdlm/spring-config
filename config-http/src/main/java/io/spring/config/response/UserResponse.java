package io.spring.config.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String role;        // 角色编码（兼容）
    private Integer roleId;     // 角色ID
    private Integer status;
    private LocalDateTime createTime;
}

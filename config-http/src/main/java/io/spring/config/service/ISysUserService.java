package io.spring.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.spring.config.domain.SysUser;

public interface ISysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     */
    SysUser login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);
}

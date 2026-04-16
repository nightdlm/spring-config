package io.spring.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.spring.config.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}

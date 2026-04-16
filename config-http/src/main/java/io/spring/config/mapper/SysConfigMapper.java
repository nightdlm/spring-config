package io.spring.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.spring.config.domain.SysConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}

package io.spring.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.spring.config.domain.ConfigHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigHistoryMapper extends BaseMapper<ConfigHistory> {
}

package io.spring.config.service.impl;

import io.spring.config.domain.ServerConfig;
import io.spring.config.mapper.ServerConfigMapper;
import io.spring.config.service.IServerConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author night
 * @since 2024-11-30
 */
@Service
public class ServerConfigServiceImpl extends ServiceImpl<ServerConfigMapper, ServerConfig> implements IServerConfigService {

}

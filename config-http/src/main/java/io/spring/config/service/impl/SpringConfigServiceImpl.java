package io.spring.config.service.impl;

import io.spring.config.domain.SpringConfig;
import io.spring.config.mapper.SpringConfigMapper;
import io.spring.config.service.ISpringConfigService;
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
public class SpringConfigServiceImpl extends ServiceImpl<SpringConfigMapper, SpringConfig> implements ISpringConfigService {

}

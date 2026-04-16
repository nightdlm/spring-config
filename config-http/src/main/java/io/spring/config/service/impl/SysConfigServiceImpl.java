package io.spring.config.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.spring.config.domain.SysConfig;
import io.spring.config.mapper.SysConfigMapper;
import io.spring.config.service.ISysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Override
    public String getConfigValue(String key) {
        SysConfig config = this.getOne(Wrappers.lambdaQuery(SysConfig.class)
                .eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional
    public void setConfigValue(String key, String value) {
        SysConfig config = this.getOne(Wrappers.lambdaQuery(SysConfig.class)
                .eq(SysConfig::getConfigKey, key));
        
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            this.save(config);
        } else {
            config.setConfigValue(value);
            this.updateById(config);
        }
    }

    @Override
    public boolean isEmailEnabled() {
        String enabled = getConfigValue("email.enabled");
        return "true".equalsIgnoreCase(enabled);
    }
}

package io.spring.config.service.impl;

import io.spring.config.service.ServerValueService;
import io.spring.config.utils.RWUtils;
import io.spring.core.param.ServerValueParam;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

@Service
public class ServerValueImpl implements ServerValueService {

    private static final String prefix = "." + File.separator +"config" + File.separator;
    private static final String suffix = ".properties";

    @Override
    public List<ServerValueParam> getServerValue(String serverName) {
        checkParamServerName(serverName);
        return RWUtils.readFile(prefix + serverName + suffix);
    }

    @Override
    public void updateServerValue(String serverName, String key, String value) {
        checkParamServerName(serverName);
        checkParamKey(key);
        final Properties properties = RWUtils.getProperties(prefix + serverName + suffix);
        properties.put(key,value);
        RWUtils.writeFile(prefix + serverName + suffix,properties);
    }

    @Override
    public void deleteServerValue(String serverName, String key) {
        checkParamServerName(serverName);
        checkParamKey(key);
        final Properties properties = RWUtils.getProperties(prefix + serverName + suffix);
        properties.remove(key);
        RWUtils.writeFile(prefix + serverName + suffix,properties);
    }

    private void checkParamServerName(String serverName) {
        if (serverName == null)
            throw new RuntimeException("服务名不能为空");
        if (serverName.trim().length() == 0)
            throw new RuntimeException("服务名不能为空");
        if (!Files.exists(new File(prefix + serverName+suffix).toPath()))
            throw new RuntimeException("服务不存在");
    }

    private void checkParamKey( String key) {
        if (key == null)
            throw new RuntimeException("键不能为空");
        if (key.trim().length() == 0)
            throw new RuntimeException("键不能为空");
    }
}

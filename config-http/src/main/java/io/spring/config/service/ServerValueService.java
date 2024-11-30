package io.spring.config.service;

import io.spring.core.param.ServerValueParam;

import java.util.List;

public interface ServerValueService {
    /**
     * 获取当前服务值中所有的键值对
     * @return 键值对
     */
    List<ServerValueParam> getServerValue(String serverName);

    /**
     * 更新服务值,没有会进行创建
     * @param serverName 服务名
     * @param key
     * @param value
     */
    void updateServerValue(String serverName, String key, String value);

    /**
     * 删除服务值
     * @param serverName
     * @param key
     */
    void deleteServerValue(String serverName, String key);

}

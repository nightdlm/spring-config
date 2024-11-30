package io.spring.config.service;

import io.spring.config.response.ApiResponse;

import java.io.File;
import java.util.List;

public interface ServerManager {

    /**
     * 创建服务
     * @param serverName 服务名称
     * @return void
     */
    void createServer(String serverName);

    /**
     * 删除服务
     * @param serverName 服务名称
     * @return 成功响应
     */
    void deleteServer(String serverName);

    /**
     * 更新服务名
     * @param old_serverName 旧服务名称
     * @param new_serverName 新名称
     * @return 成功响应
     */
    void updateServer(String old_serverName , String new_serverName);

    /**
     * 获取服务列表
     * @param subString_name 服务模糊搜索词
     * @return 服务列表
     */
    List<String> getAllServer(String subString_name);
}

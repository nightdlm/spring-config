package io.spring.config.service.impl;

import io.spring.config.service.ServerManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerNameImpl implements ServerManager {

    private static final String prefix = "." + File.separator +"config" + File.separator;
    private static final String suffix = ".properties";

    @Override
    public void createServer(String serverName) {
        if (serverName == null || "".equals(serverName.trim()))
            throw new IllegalArgumentException("serverName 不能为空");
        //读取所有的配置文件信息
        final File file = new File(prefix + serverName+suffix);
        if (file.exists()){
            throw new IllegalArgumentException("服务名重复，请重新创建");
        }
        try {
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("无法创建服务");
        }
    }

    @Override
    public void deleteServer(String serverName) {
        final File file = new File(prefix + serverName+suffix);
        if (!file.exists()){
            throw new IllegalArgumentException("服务不存在");
        }
        if (!file.delete())
            throw new RuntimeException("删除失败");
    }

    @Override
    public void updateServer(String old_serverName , String new_serverName) {
        final File file = new File(prefix + old_serverName+suffix);
        if (!file.exists()){
            throw new IllegalArgumentException("服务不存在");
        }
        final File new_file = new File(prefix + old_serverName+suffix);
        if (new_file.exists()){
            throw new IllegalArgumentException("重命名失败，存在同名服务");
        }
        if (!file.renameTo(new File(prefix + new_serverName+suffix))) {
            throw new RuntimeException("重命名失败");
        }
    }

    @Override
    public List<String> getAllServer(String subString_name) {
        final File file = new File(prefix);
        final File[] allserver = file.listFiles(File::isFile);
        if (allserver == null)
            return Collections.emptyList();
        if (subString_name == null || "".equals(subString_name.trim())){
            final ArrayList<String> serve_name_list = new ArrayList<>();
            Arrays.stream(allserver).collect(Collectors.toList()).forEach(item-> serve_name_list.add(item.getName().replace(suffix, "")));
            return serve_name_list;
        } else {
            final ArrayList<String> serve_name_list = new ArrayList<>();
            Arrays.stream(allserver).collect(Collectors.toList()).stream().filter(item->item.getName().contains(subString_name)).forEach(item-> serve_name_list.add(item.getName().replace(suffix, "")));
            return serve_name_list;
        }
    }
}

//package io.spring.config.controller;
//
//import com.alibaba.fastjson2.JSON;
//import io.spring.config.request.UpdateConfig;
//import io.spring.config.response.ApiResponse;
//import io.spring.config.service.ServerManager;
//import io.spring.core.param.ServerValueParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.io.*;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
// */
//@RestController
//@RequestMapping("/api")
//@Valid
//public class BasicController {
//
//    @Autowired
//    private ServerManager serverManager;
//
//    @GetMapping("create")
//    public ApiResponse<Void> createConfig(String serverName){
//        return serverManager.createServer(serverName);
//    }
//
//    @GetMapping("info")
//    public ApiResponse<Map<String, ServerValueParam>> getConfigInfo(String serverName) throws IOException {
//        if (serverName == null)
//            throw new IllegalArgumentException("serverName 不能为空");
//        //读取所有的配置文件信息
//        final File file = new File(prefix + serverName+suffix);
//        if (!file.exists()){
//            throw new IllegalArgumentException("服务不存在");
//        }
//        //读取properties配置文件
//        try (FileInputStream fis = new FileInputStream(file)) {
//            Properties properties = new Properties();
//            properties.load(fis);
//            // 构建键值对的Map
//            Map<String, ServerValueParam> configMap = new HashMap<>();
//            for (String key : properties.stringPropertyNames()) {
//                configMap.put(key,JSON.parseObject(properties.getProperty(key), ServerValueParam.class));
//            }
//            return ApiResponse.of(configMap);
//        }
//    }
//
//    @PostMapping("update")
//    public ApiResponse<Void> updateInfo(@RequestBody @Validated(UpdateConfig.Update.class) UpdateConfig updateConfig) throws IOException, IllegalAccessException {
//        // 读取所有的配置文件信息
//        final File file = new File(prefix + updateConfig.getServerName() + suffix);
//        if (!file.exists()) {
//            throw new IllegalArgumentException("服务不存在");
//        }
//        //读取properties文件
//        try (FileInputStream fis = new FileInputStream(file)) {
//            Properties properties = new Properties();
//            properties.load(fis);
//            final ServerValueParam param = new ServerValueParam();
//            param.setValue(updateConfig.getValue());
//            param.setType(updateConfig.getType());
//            param.setDesc(updateConfig.getDesc());
//            param.setUpdateTime(LocalDateTime.now());
//            properties.put(updateConfig.getKey(),JSON.toJSONString(param));
//            try (Writer fos = new FileWriter(file)) {
//                properties.store(fos, "update");
//            }
//            if (contentManager.getOrDefault(updateConfig.getKey(),null)!=null){
//                contentManager.get(updateConfig.getKey()).set(null,updateConfig.getValue());
//            }
//            redisTemplate.convertAndSend( updateConfig.getServerName(), JSON.toJSONString(param));
//        }
//
//        return ApiResponse.success();
//    }
//
//
//    @PostMapping("delete")
//    public ApiResponse<Map<String,Object>> deleteInfo(@RequestBody @Validated(UpdateConfig.Delete.class) UpdateConfig updateConfig) throws IOException {
//        // 读取所有的配置文件信息
//        final File file = new File(prefix + updateConfig.getServerName() + suffix);
//        if (!file.exists()) {
//            throw new IllegalArgumentException("服务不存在");
//        }
//        //读取properties文件
//        try (FileInputStream fis = new FileInputStream(file)) {
//            Properties properties = new Properties();
//            properties.load(fis);
//
//            properties.remove(updateConfig.getKey());
//            try (Writer fos = new FileWriter(file)) {
//                properties.store(fos, "delete");
//            }
//        }
//        return ApiResponse.success();
//    }
//
//}

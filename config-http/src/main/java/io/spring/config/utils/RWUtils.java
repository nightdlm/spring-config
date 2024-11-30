package io.spring.config.utils;

import com.alibaba.fastjson2.JSON;
import io.spring.core.param.ServerValueParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class RWUtils {
    private RWUtils() {
    }

    public static Properties getProperties(String path){
        Properties properties = new Properties();
        //读取properties配置文件
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置读取异常");
        }
    }

    public static List<ServerValueParam> readFile(String path){
        Properties properties = new Properties();
        //读取properties配置文件
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            // 构建键值对的Map
            List<ServerValueParam> configList = new ArrayList<>();
            for (String key : properties.stringPropertyNames()) {
                configList.add(JSON.parseObject(properties.getProperty(key), ServerValueParam.class));
            }
            return configList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置读取异常");
        }
    }


    public static void writeFile(String path, Properties properties){
        try (Writer fos = new FileWriter(path)) {
            properties.store(fos, "write");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置更新异常");
        }
    }
}

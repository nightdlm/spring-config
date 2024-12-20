package io.spring.core.component;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisMessageListener implements MessageListener {

    @Autowired
    private Environment environment;

    @Resource
    private Map<String, Field> contentManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            Map parseObject = JSON.parseObject(body, Map.class);
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) parseObject.entrySet().iterator().next();
            if (contentManager.getOrDefault(entry.getKey(),null)==null) return;
            Field field = contentManager.get(entry.getKey());
            field.set(null,convert(field,entry.getValue().toString()));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //基础类型转换
    public static <T> T convert(Field field, String value ) {
        final Class<?> targetType = field.getType();
        try {
            if (targetType == String.class) {
                return (T) value;
            } else if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(value);
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(value);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(value);
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(value);
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(value);
            } else if (targetType == Character.class || targetType == char.class) {
                return (T) Character.valueOf(value.charAt(0));
            } else if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(value);
            } else if (targetType == List.class) {
                return (T)JSON.parseObject(value,List.class);
            } else if (targetType == Map.class) {
                return (T)JSON.parseObject(value,Map.class);
            }
            else {
                throw new IllegalArgumentException("Unsupported target type: " + targetType);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, RedisMessageListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        final String property = environment.getProperty("spring.config.dynamic.server-name");
        if (property==null)
            throw new RuntimeException("spring.config.dynamic.server-name"+" must exists");
        container.addMessageListener(new MessageListenerAdapter(listener), new PatternTopic(property));
        return container;
    }
}
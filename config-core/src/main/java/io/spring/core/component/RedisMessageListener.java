package io.spring.core.component;

import com.alibaba.fastjson2.JSON;
import io.spring.core.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

@ConditionalOnProperty(prefix = "spring.config.dynamic",name = "enable-remote",havingValue = "true")
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
            ConfigUtils.convert(field,entry.getValue().toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, RedisMessageListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(listener), new PatternTopic(environment.getRequiredProperty("spring.config.dynamic.server-name")));
        return container;
    }
}
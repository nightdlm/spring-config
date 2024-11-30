package io.spring.core.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class RedisConfig {

    @Bean
    public Map<String, Field> contentManager(){
        return new HashMap<>();
    }

    @Autowired
    private Environment environment;

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, RedisMessageListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(listener), new PatternTopic(environment.getProperty("spring.application.name")));
        return container;
    }
}

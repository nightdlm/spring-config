package io.spring.core.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.core.utils.ConfigUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.lang.reflect.Field;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "spring.config.dynamic",name = "enable-remote",havingValue = "true")
public class RedisMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private Environment environment;

    @Resource
    private Map<String, Field> contentManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            logger.debug("Received redis message: {}", body);
            
            Map<String, Object> parseObject = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            Map.Entry<String, Object> entry = parseObject.entrySet().iterator().next();
            
            if (contentManager.getOrDefault(entry.getKey(),null)==null) {
                logger.debug("No field found for key: {}", entry.getKey());
                return;
            }
            
            Field field = contentManager.get(entry.getKey());
            ConfigUtils.convert(field, entry.getValue().toString());
            logger.info("Successfully updated config field: {} = {}", entry.getKey(), entry.getValue());
        } catch (Exception e) {
            logger.error("Failed to process redis message", e);
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
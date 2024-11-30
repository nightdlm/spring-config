package io.spring.core.component;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        System.out.println(body);
    }
}
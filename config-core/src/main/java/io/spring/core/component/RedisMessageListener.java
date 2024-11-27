package io.spring.core.component;


import com.alibaba.fastjson2.JSON;
import io.spring.core.param.ServerValueParam;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        final ServerValueParam serverValueParam = JSON.parseObject(body, ServerValueParam.class);
        System.out.println("updateConfig:"+serverValueParam.toString());
    }
}
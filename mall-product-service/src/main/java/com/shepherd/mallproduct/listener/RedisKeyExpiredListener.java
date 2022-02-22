package com.shepherd.mallproduct.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/13 11:21
 */
@Slf4j
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {
    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 注意：监听redis的key功能需要在redis的配置文件redis.conf中开启notify-keyspace-events "Ex"
     * 这里只能拿到过期的key，不能拿到value值，如果要监听过去key，并获取value值，我们可以通过两个key操作
     * 通过监听key，并将value保存在key2中，当key过期时候去拿到key的存的value值，并删除key2即可
     * 还需要注意：redis的过期key不一定准时自动删除的，redis的删除过期key策略：惰性删除+定期删除

     * 惰性删除
     * 客户端在访问key的时候，对key的过期时间进行校验，如果过期了就立即删除
     *
     * 定期删除
     * Redis会将设置了过期时间的key放在一个独立的字典中，定时遍历这个字典来删除过期的key，遍历策略如下
     * 每秒进行10次过期扫描，每次从过期字典中随机选出20个key
     * 删除20个key中已经过期的key
     * 如果过期key的比例超过1/4，则进行步骤一
     * 每次扫描时间的上限默认不超过25ms，避免线程卡死
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("监听到过期key:{}" + message.toString());
        byte[] channel = message.getChannel();
        byte[] body = message.getBody();
        log.info("message channel: {}", new String(channel, StandardCharsets.UTF_8));
        log.info("message body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("pattern: {}", new String(pattern, StandardCharsets.UTF_8));
    }
}

package com.shepherd.mallorder.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/6 23:05
 */
@Component
@Slf4j
public class TestReceiveMessage {

    @RabbitListener(queues = {"test-queue1"})
    public void receiveMessage(Object msg) {
        log.info("接收到消息内容：{}", msg);

    }
}

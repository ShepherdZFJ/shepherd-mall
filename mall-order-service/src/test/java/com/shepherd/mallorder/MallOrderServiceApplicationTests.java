package com.shepherd.mallorder;

import com.shepherd.mallorder.dto.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MallOrderServiceApplicationTests {
    @Resource
    private AmqpAdmin amqpAdmin;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void createDirectExchange() {
        amqpAdmin.declareExchange(new DirectExchange("code-direct-exchange"));
    }

    @Test
    public void createQueue() {
        amqpAdmin.declareQueue(new Queue("test-queue1"));
    }

    @Test
    public void binding() {
        amqpAdmin.declareBinding(new Binding("test-queue1", Binding.DestinationType.QUEUE, "code-direct-exchange", "direct.key", null));
    }

    @Test
    public void sendMessage() {
        String msg = "hello, shepherd发送的消息";
        rabbitTemplate.convertAndSend("code-direct-exchange", "direct.key", msg);
        log.info("消息发送完成：{}", msg);
    }

    @Test
    public void sendMsgObject() {
        CartItem cartItem = new CartItem();
        cartItem.setName("苹果12pro");
        cartItem.setNumber(2);
        cartItem.setSpecValues(Lists.newArrayList("256G", "红色", "M1芯片"));
        rabbitTemplate.convertAndSend("code-direct-exchange", "direct.key", cartItem);
        log.info("消息对象发送完成：{}", cartItem);


    }


    @Test
    public void contextLoads() {
    }


}

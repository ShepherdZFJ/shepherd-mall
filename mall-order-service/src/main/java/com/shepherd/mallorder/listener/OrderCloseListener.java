package com.shepherd.mallorder.listener;

import com.rabbitmq.client.Channel;
import com.shepherd.mallorder.api.service.OrderService;
import com.shepherd.mallorder.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/11 18:55
 */

@Component
@Slf4j
@RabbitListener(queues = {"order.release.order.queue"})
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderDTO orderDTO, Message message, Channel channel) throws IOException {
        log.info("收到过期的订单信息，准备关闭订单" + orderDTO.getOrderNo());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            orderService.closeOrder(orderDTO);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e){
            //basicNack与basicReject一样，只是多了一个参数控制是否可以批量拒绝
            //channel.basicNack(deliveryTag, true, true);
            channel.basicReject(deliveryTag,true);
        }

    }
}

package com.shepherd.ware.listener;

import com.rabbitmq.client.Channel;
import com.shepherd.ware.api.service.WareSkuService;
import com.shepherd.ware.dto.Order;
import com.shepherd.ware.dto.WareOrderTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/12 16:18
 */

@Slf4j
@Component
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Resource
    private WareSkuService wareSkuService;


    @RabbitHandler
    public void releaseStockFromWare(WareOrderTaskDTO wareOrderTaskDTO, Message message, Channel channel) throws IOException {
        log.info("<<============收到库存解锁的消息=================>>");
        try {
            wareSkuService.releaseStock(wareOrderTaskDTO);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void releaseStockFromOrder(Order order, Message message, Channel channel) throws IOException {
        log.info("<<=========从订单模块收到库存解锁的消息==========>>");
        try {
            wareSkuService.releaseStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}

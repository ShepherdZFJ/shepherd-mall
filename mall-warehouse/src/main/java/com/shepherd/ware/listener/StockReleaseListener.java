package com.shepherd.ware.listener;

import com.alibaba.fastjson.JSONObject;
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
            log.error("消费信息失败：", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    /**
     * 使用@RabbitHandler接收同一队列不同消息时，其映射的消息对象和发送者发送的对象必须同一包名类名，
     * 否则会报错：org.springframework.amqp.support.converter.MessageConversionException: failed to resolve class name
     * 解决方案：1.使用公共类，但是如果是跨服务这种不太方便
     *         2.发送消息时候交消息对象转换为JSONObject，拿到消息之后在转换成对应的对象
     * @param content
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void releaseStockFromOrder(String content, Message message, Channel channel) throws IOException {
        log.info("<<=========从订单模块收到库存解锁的消息==========>>");
        try {
            Order order = JSONObject.parseObject(content, Order.class);
            wareSkuService.releaseStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("消费信息失败：", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}

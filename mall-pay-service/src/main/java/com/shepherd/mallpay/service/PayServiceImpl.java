package com.shepherd.mallpay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallpay.api.service.PayService;
import com.shepherd.mallpay.dao.PayInfoDAO;
import com.shepherd.mallpay.dto.PayInfoDTO;
import com.shepherd.mallpay.entity.PayInfo;
import com.shepherd.mallpay.enums.PayPlatformEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 15:44
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {
    private final static String QUEUE_PAY_NOTIFY = "payNotify";
    @Resource
    private BestPayService bestPayService;
    @Resource
    private PayInfoDAO payInfoDAO;
    @Resource
    private AmqpTemplate amqpTemplate;

    @Override
    public PayResponse create(String orderNo, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.setOrderNo(orderNo);
        payInfoDTO.setPayPlatform(PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode());
        payInfoDTO.setPayStatus(OrderStatusEnum.NOTPAY.name());
        payInfoDTO.setPayAmount(amount);
        payInfoDTO.setIsDelete(CommonConstant.NOT_DEL);
        payInfoDTO.setCreateTime(new Date());
        payInfoDTO.setUpdateTime(new Date());
        PayInfo payInfo = MallBeanUtil.copy(payInfoDTO, PayInfo.class);
        payInfoDAO.insert(payInfo);


        PayRequest payRequest = new PayRequest();
        payRequest.setOrderName("shepherd-最好的sdk");
        payRequest.setOrderId(orderNo);
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("发起支付 payResponse ={}", payResponse);
        return payResponse;
    }


    @Override
    public String asyncNotify(String notifyData) {
        //1. 签名检验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse={}", payResponse);

        //2. 金额校验（从数据库查订单）
        //比较严重（正常情况下是不会发生的）发出告警：钉钉、短信
        QueryWrapper<PayInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", payResponse.getOrderId());
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        PayInfo payInfo = payInfoDAO.selectOne(queryWrapper);
        if (payInfo == null) {
            //告警
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }

        //如果订单支付状态不是"已支付"
        if (!payInfo.getPayStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            //Double类型比较大小，精度。1.00  1.0
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                //告警
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo=" + payResponse.getOrderId());
            }

            //3. 修改订单支付状态
            payInfo.setPayStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfo.setUpdateTime(new Date());
            payInfoDAO.updateById(payInfo);
        }

        //TODO pay发送MQ消息，mall接受MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));

        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            //4. 告诉微信不要再通知了
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        }

        throw new RuntimeException("异步通知中错误的支付平台");

    }

    @Override
    public void queryByOrderId(String orderId) {

    }
}

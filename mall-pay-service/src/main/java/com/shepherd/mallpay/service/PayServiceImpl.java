package com.shepherd.mallpay.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallpay.api.service.PayService;
import com.shepherd.mallpay.constant.PayConstant;
import com.shepherd.mallpay.dao.PayInfoDAO;
import com.shepherd.mallpay.dto.AlipayNotify;
import com.shepherd.mallpay.dto.Order;
import com.shepherd.mallpay.dto.PayInfoDTO;
import com.shepherd.mallpay.entity.PayInfo;
import com.shepherd.mallpay.enums.AlipayNotifyStatusEnum;
import com.shepherd.mallpay.enums.PayPlatformEnum;
import com.shepherd.mallpay.feign.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

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
    @Resource
    private OrderService orderService;

    @Override
    public PayResponse create(String orderNo, Integer payType) {

        //1.根据微订单号先查询订单详情
        ResponseVO<Order> responseVO = orderService.getOrderByOrderNo(orderNo);
        Order order = responseVO.getData();


        //2.发起支付 目前这个这个sdk不支持设置支付的过期时间，收单防止在订单自动关闭同时支付，导致订单关闭但是却付款了。。。
        PayRequest payRequest = new PayRequest();
        payRequest.setOrderId(orderNo);
        payRequest.setOrderAmount(order.getPayAmount().doubleValue());
        if (Objects.equals(payType, PayConstant.TYPE_ALIPAY)) {
            payRequest.setPayTypeEnum(BestPayTypeEnum.ALIPAY_PC);
        }
        if (Objects.equals(payType, PayConstant.TYPE_WXPAY)) {
            payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        }
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("发起支付 payResponse ={}", payResponse);
        return payResponse;
    }


    @Override
    public String asyncNotify(String notifyData) {
        //1. 签名检验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse={}", payResponse);

        //2.插入付款记录


        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            //4. 告诉微信不要再通知了
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            handAlipayAsyncNotify(notifyData);
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");

    }

    private void handAlipayAsyncNotify(String notifyData) {
        AlipayNotify alipayNotify = JSONObject.parseObject(notifyData, AlipayNotify.class);
        //保存交易流水
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(alipayNotify.getOut_trade_no());
        payInfo.setTradeNo(alipayNotify.getTrade_no());
        payInfo.setSubject(alipayNotify.getSubject());
        payInfo.setCallbackTime(alipayNotify.getNotify_time());
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());
        payInfo.setIsDelete(CommonConstant.NOT_DEL);
        payInfo.setType(PayConstant.TYPE_ALIPAY);
        String tradeStatus = alipayNotify.getTrade_status();
        AlipayNotifyStatusEnum alipayNotifyStatusEnum = AlipayNotifyStatusEnum.getAlipayNotifyStatusEnum(tradeStatus);
        payInfo.setStatus(alipayNotifyStatusEnum.getStatus());
        payInfoDAO.insert(payInfo);
    }

    private void handWechatPayAsyncNotify(String notifyData) {
        // todo
    }

    @Override
    public PayInfoDTO getPayInfoByOrderNo(String orderNo) {
        LambdaQueryWrapper<PayInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PayInfo::getOrderNo, orderNo);
        lambdaQueryWrapper.eq(PayInfo::getIsDelete, CommonConstant.NOT_DEL);
        PayInfo payInfo = payInfoDAO.selectOne(lambdaQueryWrapper);
        return toPayInfoDTO(payInfo);

    }

    private PayInfoDTO toPayInfoDTO(PayInfo payInfo) {
        if (payInfo == null) {
            return null;
        }
        PayInfoDTO payInfoDTO = MallBeanUtil.copy(payInfo, PayInfoDTO.class);
        payInfoDTO.setPayInfoId(payInfo.getId());
        return payInfoDTO;
    }
}

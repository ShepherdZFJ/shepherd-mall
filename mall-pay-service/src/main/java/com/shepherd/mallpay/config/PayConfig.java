package com.shepherd.mallpay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/10/8 22:40
 */
@Component
public class PayConfig {
    @Resource
    private WxProperties wxProperties;

    @Resource
    private AlipayProperties alipayProperties;

    @Bean
    public BestPayService bestPayService(AliPayConfig aliPayConfig, WxPayConfig wxPayConfig) {

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }

    @Bean
    public AliPayConfig aliPayConfig(){
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(alipayProperties.getAppId());
        aliPayConfig.setPrivateKey(alipayProperties.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(alipayProperties.getPublicKey());
        aliPayConfig.setNotifyUrl(alipayProperties.getNotifyUrl());
        aliPayConfig.setReturnUrl(alipayProperties.getReturnUrl());
        return aliPayConfig;
    }

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxProperties.getAppId());
        wxPayConfig.setMchId(wxProperties.getMchId());
        wxPayConfig.setMchKey(wxProperties.getMchKey());
        //192.168.50.101 同一局域网可访问
        //125.121.56.227 云服务器可行，家庭宽带不行(路由器、光猫)
        wxPayConfig.setNotifyUrl(wxProperties.getNotifyUrl());
        wxPayConfig.setReturnUrl(wxProperties.getReturnUrl());
        return wxPayConfig;
    }
}

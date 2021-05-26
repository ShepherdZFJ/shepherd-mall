package com.shepherd.mallorder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/5/20 18:44
 */
@Configuration
public class ApplicationListenerConfig implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuartzBindOperationConfig quartzBindOperationConfig;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        quartzBindOperationConfig.scheduleBind();
    }

}

package com.shepherd.malluser.test;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/2/23 16:59
 */
@Component
public class MyInitBean implements InitializingBean {
    @Resource
    private RunService runService;

    @Override
    public void afterPropertiesSet() throws Exception {
        runService.run();
    }
}

package com.shepherd.mallproduct.nacos;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/4/20 15:06
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserTest {
    @Resource
    private User user;
    @Resource
    private UserProperties userProperties;

    @Test
    public void test() {
        System.out.println(userProperties.getName());
        System.out.println(user);
    }

}
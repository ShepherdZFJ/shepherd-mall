package com.shepherd.mallproduct.listener;

import com.alibaba.fastjson.JSONObject;
import com.shepherd.mallproduct.entity.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/13 11:33
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisKeyExpiredListenerTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Test
    public void test() {
        Brand brand = new Brand();
        brand.setName("苹果");
        brand.setDescription("这个不是吃的苹果，是iPhone apple公司");
        brand.setCategoryId(88l);
        brand.setCreateTime(new Date());
        brand.setIsDelete(0);
        stringRedisTemplate.opsForValue().set("test_expire", JSONObject.toJSONString(brand), 10, TimeUnit.SECONDS);
    }

}
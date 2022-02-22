package com.shepherd.malluser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/2/21 17:40
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisSentinelTest {


    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void test001() throws Exception {
        while (true) {
            String key = "time:" + new Date().getTime();
            redisTemplate.opsForValue().set(key, new Date().getTime(),5, TimeUnit.MINUTES);
            TimeUnit.SECONDS.sleep(5);
            System.out.println(redisTemplate.opsForValue().get(key));
        }
    }
}

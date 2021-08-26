package com.shepherd.mall.seckill.schedule;

import com.shepherd.mall.seckill.api.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/26 15:21
 */
@Component
@Slf4j
public class SeckillSchedule {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeckillService seckillService;

    //秒杀商品上架功能的锁
    private final String upload_lock = "seckill:upload:lock";

    /**
     * 定时任务
     * 每天三点上架最近三天的秒杀商品
     */

//    @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(cron = "0 */1 * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        //为避免分布式情况下多服务同时上架的情况，使用分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        try {
            lock.lock(100, TimeUnit.SECONDS);
            seckillService.upSeckillSessionLast3Day();
        }catch (Exception e){
            log.error("定时任务发生错误：", e);
        }finally {
            lock.unlock();
        }
    }
}

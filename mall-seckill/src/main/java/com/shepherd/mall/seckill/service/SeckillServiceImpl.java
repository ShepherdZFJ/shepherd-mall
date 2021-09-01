package com.shepherd.mall.seckill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.seckill.dto.SeckillOrder;
import com.shepherd.mall.seckill.dto.SeckillSessionDTO;
import com.shepherd.mall.seckill.api.service.SeckillService;
import com.shepherd.mall.seckill.dao.SeckillSessionDAO;
import com.shepherd.mall.seckill.dao.SeckillSkuDAO;
import com.shepherd.mall.seckill.dto.SeckillSkuDTO;
import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.seckill.entity.SeckillSession;
import com.shepherd.mall.seckill.entity.SeckillSku;
import com.shepherd.mall.seckill.feign.ProductService;
import com.shepherd.mall.utils.DateUtil;
import com.shepherd.mall.utils.IdWorker;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/25 23:52
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {
    @Resource
    private SeckillSessionDAO seckillSessionDAO;
    @Resource
    private SeckillSkuDAO seckillSkuDAO;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ProductService productService;
    @Resource
    private IdWorker idWorker;
    @Resource
    private RabbitTemplate rabbitTemplate;

    //K: SESSION_CACHE_PREFIX + startTime + "_" + endTime
    //V: sessionId+"-"+skuId的List
    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    //K: 固定值SECKILL_CHARE_PREFIX
    //V: hash，k为sessionId+"-"+skuId，v为对应的商品信息SeckillSkuRedisTo
    private final String SECKILL_SKU_CACHE = "seckill:skus";

    //K: SKU_STOCK_SEMAPHORE+商品随机码
    //V: 秒杀的库存件数
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码


    @Override
    public void upSeckillSessionLast3Day() {
        List<SeckillSession> seckillSessionList = getSeckillSessionLast3Day();
        List<Long> sessionIds = seckillSessionList.parallelStream().map(SeckillSession::getId).collect(Collectors.toList());
        List<SeckillSku> seckillSkuList = getSeckillSkuList(sessionIds);
        List<SeckillSessionDTO> seckillSessionDTOS = new ArrayList<>();
        seckillSessionList.forEach(seckillSession -> {
            SeckillSessionDTO seckillSessionDTO = MallBeanUtil.copy(seckillSession, SeckillSessionDTO.class);
            List<SeckillSku> skus = seckillSkuList.parallelStream().filter(seckillSku -> Objects.equals(seckillSku.getSessionId(), seckillSessionDTO.getId())).collect(Collectors.toList());
            seckillSessionDTO.setSkuList(skus);
            seckillSessionDTOS.add(seckillSessionDTO);
        });
        saveSeckillSessionToRedis(seckillSessionDTOS);
        saveSeckillSkuToRedis(seckillSessionDTOS);

    }

    @Override
    public SeckillSessionDTO getCurrentSeckillSessionInfo() {
        long currentTime = System.currentTimeMillis();
        return null;
        //todo :逻辑暂时不定
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SECKILL_SKU_CACHE);
        String str = ops.get(killId);
        String orderNo = null;
        if (StringUtils.isNotBlank(str)){
            SeckillSkuDTO seckillSkuDTO = JSON.parseObject(str, SeckillSkuDTO.class);
            //1. 验证时效
            long current = System.currentTimeMillis();
            if (current >= seckillSkuDTO.getStartTime() && current <= seckillSkuDTO.getEndTime()) {
                //2. 验证商品和商品随机码是否对应
                String redisKey = seckillSkuDTO.getSessionId() + "-" + seckillSkuDTO.getSkuId();
                if (redisKey.equals(killId) && seckillSkuDTO.getRandomCode().equals(key)) {
                    //3. 验证当前用户是否购买过
                   // MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
                    long ttl = seckillSkuDTO.getEndTime() - System.currentTimeMillis();
                    Long userId = 1l;
                    //3.1 通过在redis中使用 用户id-skuId 来占位看是否买过
                    Boolean flag = redisTemplate.opsForValue().setIfAbsent(userId +"-"+seckillSkuDTO.getSkuId(), num.toString(), ttl, TimeUnit.MILLISECONDS);
                    //3.2 占位成功，说明该用户未秒杀过该商品，则继续
                    if (flag){
                        //4. 校验库存和购买量是否符合要求
                        if (num <= seckillSkuDTO.getLimitNumber()) {
                            //4.1 尝试获取库存信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + seckillSkuDTO.getRandomCode());
                            boolean acquire = false;
                            try {
                                acquire = semaphore.tryAcquire(num,100, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                log.error("redission获取库存信号量失败：", e);

                            }
                            //4.2 获取库存成功
                            if (acquire) {
                                //5. 发送消息创建订单
                                long l = idWorker.nextId();
                                String currentDateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSS");
                                orderNo = String.valueOf(l) + currentDateStr;
                                //5.2 创建秒杀订单to
                                SeckillOrder order = new SeckillOrder();
                                order.setUserId(userId);
                                order.setNumber(num);
                                order.setOrderNo(orderNo);
                                order.setSessionId(seckillSkuDTO.getSessionId());
                                order.setSeckillPrice(seckillSkuDTO.getSeckillPrice());
                                order.setSkuId(seckillSkuDTO.getSkuId());
                                //5.3 发送创建订单的消息
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", order);
                            }
                        }
                    }
                }
            }
            return orderNo;
        }
        return null;
    }

    /**
     * 获取最近三天的秒杀活动场次信息，获取三天是为了可以知道秒杀预告信息可以提前告诉用户
     * @return
     */
    List<SeckillSession> getSeckillSessionLast3Day() {
        LambdaQueryWrapper<SeckillSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillSession::getIsDelete, CommonConstant.NOT_DEL);
        queryWrapper.between(SeckillSession::getStartTime, DateUtil.getStartTime(), DateUtil.getEndTime(2));
        List<SeckillSession> seckillSessions = seckillSessionDAO.selectList(queryWrapper);
        return seckillSessions;
    }

    /**
     * 根据秒杀场次id，查询对应场次的商品
     * @param sessionIds
     * @return
     */
    List<SeckillSku> getSeckillSkuList(List<Long> sessionIds) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SeckillSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SeckillSku::getSessionId, sessionIds);
        queryWrapper.eq(SeckillSku::getIsDelete, CommonConstant.NOT_DEL);
        List<SeckillSku> seckillSkus = seckillSkuDAO.selectList(queryWrapper);
        return seckillSkus;
    }

    void saveSeckillSessionToRedis(List<SeckillSessionDTO> seckillSessions) {
        if (CollectionUtils.isEmpty(seckillSessions)) {
            return;
        }
        seckillSessions.forEach(seckillSession -> {
            String key = SESSION_CACHE_PREFIX + seckillSession.getStartTime().getTime() + "_" + seckillSession.getEndTime().getTime();
            //判断当前key是否存在
            if (!redisTemplate.hasKey(key)) {
                ListOperations opsForList = redisTemplate.opsForList();
                List<String> values = seckillSession.getSkuList().stream()
                        .map(sku -> sku.getSessionId() +"-"+ sku.getSkuId())
                        .collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,values);
            }
        });
    }

    void saveSeckillSkuToRedis(List<SeckillSessionDTO> seckillSessions) {
        if (CollectionUtils.isEmpty(seckillSessions)) {
            return;
        }
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SECKILL_SKU_CACHE);
        seckillSessions.forEach(session->{
            session.getSkuList().stream().forEach(seckillSku->{
                Long skuId = seckillSku.getSkuId();
                String key = seckillSku.getSessionId() +"-"+ skuId;
                if (!ops.hasKey(key)){
                    SeckillSkuDTO seckillSkuDTO = MallBeanUtil.copy(seckillSku, SeckillSkuDTO.class);
                    seckillSkuDTO.setStartTime(session.getStartTime().getTime());
                    seckillSkuDTO.setEndTime(session.getEndTime().getTime());
                    try {
                        ResponseVO<SkuInfo> responseVO = productService.getSku(skuId);
                        SkuInfo skuInfo = responseVO.getData();
                        seckillSkuDTO.setSkuInfo(skuInfo);
                    } catch (Exception e) {
                        log.error("调用商品详情接口失败", e);
                        throw new BusinessException("调用商品详情接口失败");
                    }
                    //生成商品随机码，防止恶意攻击
                    String token = UUID.randomUUID().toString().replace("-", "");
                    seckillSkuDTO.setRandomCode(token);
                    //5. 序列化为json并保存
                    String jsonString = JSON.toJSONString(seckillSkuDTO);
                    ops.put(key,jsonString);
                    //5. 使用库存作为Redisson信号量限制库存
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSku.getTotalNumber());
                }
            });
        });

    }
}

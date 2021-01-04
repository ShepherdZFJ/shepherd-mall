package com.shepherd.mallproduct.cache;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shepherd.mallproduct.cache.listener.CompositeCacheRemovalListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/4 20:08
 */
@Slf4j
@Component
public class CompositeCacheManager implements CacheManager {

    private static final String CAFFEINE_CACHE_NAME = "product_cache";
    private static final String REDIS_CACHE_NAME = "product_redis_cache";
    private static final String DEFAULT_THREAD_NAME_PREFIX = "caffeine-worker-";
    private static final String DEFAULT_CACHE_ACCESS_EXPIRE_UNIT = ChronoUnit.MINUTES.name();
    private static final Integer DEFAULT_CACHE_ACCESS_EXPIRE_TIME = 10;
    private static final Double DEFAULT_JVM_MEMORY_THRESHOLD_RATIO = 0.2;
    private static final Double DEFAULT_CACHE_MAXIMUM_THRESHOLD_RATIO = 0.6;
    private static final Double DEFAULT_CACHE_INITIAL_RATIO = 0.3;
    private static final Double DEFAULT_CACHE_WATER_LEVEL_RATIO = 0.4;

    /* ************************************ caffeine variables ************************************ */

    /** 缓存读事件过期时间 */
    @Value("${composite.caffeine.access-expire-time}")
    private Integer cacheAccessExpireTime;

    /** 缓存读事件过期时间单位 */
    @Value("${composite.caffeine.access-expire-unit}")
    private String cacheAccessExpireUnit;

    /** 缓存容器所占内存与jvm进程内存阈值比率, 默认0.1 */
    @Value("${composite.caffeine.jvm-threshold-ratio}")
    private Double jvmMemoryThresholdRatio;

    /** 缓存容器最大缓存阈值比率, 默认0.6 */
    @Value("${composite.caffeine.maximum-threshold-ratio}")
    private Double cacheMaximumThresholdRatio;

    /** 缓存容器初始缓存比率(相对于最大缓存, 默认0.3) */
    @Value("${composite.caffeine.initial-ratio}")
    private Double cacheInitialRatio;

    /** 缓存容器水位线比率(相对于最大缓存, 默认0.4) */
    @Value("${composite.caffeine.water-level-ratio}")
    private Double cacheWaterLevelRatio;

    /** cache工作线程名前缀 */
    @Value("${composite.caffeine.thread-name-prefix}")
    private String cacheThreadNamePrefix;

    /** 缓存过期命中水位线 hits */
    @Value("${composite.caffeine.hits-count}")
    private Integer hitsCount;

    /** 缓存过期命中水位线 hits-active */
    @Value("${composite.caffeine.hits-count-min}")
    private Integer hitsCountActive;

    /** 缓存初始容量 */
    private Integer cacheInitialCapacity;
    /** 缓存最大容量 */
    private Integer cacheMaximumCapacity;


    /* ************************************ redis variables ************************************ */

    @Value("${composite.redis.preload-second-time}")
    private long preloadSecondTime;
    @Value("${composite.redis.refresh-expire-time}")
    private long refreshExpireTime;

    @Bean
    ExecutorService cacheExecutor() {
        return Executors.newFixedThreadPool(4, ThreadFactoryBuilder.create().setNamePrefix(cacheThreadNamePrefix).build());
    }

    @Bean
    RedisCache redisCache(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisCacheEx redisCacheEx = new RedisCacheEx(REDIS_CACHE_NAME,
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory), defaultCacheConfig());
        return redisCacheEx;
    }


    @Bean
    CaffeineCache caffeineCache(@Autowired Executor cacheExecutor) {
        if (Objects.isNull(cacheAccessExpireTime)) {
            cacheAccessExpireTime = DEFAULT_CACHE_ACCESS_EXPIRE_TIME;
        }
        if (Objects.isNull(cacheAccessExpireUnit)) {
            cacheAccessExpireUnit = DEFAULT_CACHE_ACCESS_EXPIRE_UNIT;
        }
        if (Objects.isNull(cacheMaximumThresholdRatio)) {
            cacheMaximumThresholdRatio = DEFAULT_CACHE_MAXIMUM_THRESHOLD_RATIO;
        }
        if (Objects.isNull(cacheInitialRatio)) {
            cacheInitialRatio = DEFAULT_CACHE_INITIAL_RATIO;
        }
        if (Objects.isNull(cacheWaterLevelRatio)) {
            cacheWaterLevelRatio = DEFAULT_CACHE_WATER_LEVEL_RATIO;
        }
        if (Objects.isNull(cacheThreadNamePrefix)) {
            cacheThreadNamePrefix = DEFAULT_THREAD_NAME_PREFIX;
        }

        // the total of memory in the Java virtual machine * 10%
        cacheMaximumCapacity = (int) (Runtime.getRuntime().totalMemory() * jvmMemoryThresholdRatio);
        cacheInitialCapacity = (int) (cacheMaximumCapacity * cacheInitialRatio);
        final CompositeCacheRemovalListener redisLocalCacheRemovalListener = CompositeCacheRemovalListener.builder()
                .cacheManager(this)
                .build();
        CaffeineCache caffeineCache = new CaffeineCache(CAFFEINE_CACHE_NAME, Caffeine.newBuilder()
                // 设置初始缓存大小
                .initialCapacity(cacheInitialCapacity)
                // 设置最大缓存
                .maximumSize(cacheMaximumCapacity)
                // 设置线程池
                .executor(cacheExecutor)
                // 监听器(超出最大缓存)
                .removalListener(redisLocalCacheRemovalListener)
                // 设置缓存读时间的过期时间, 默认为10分钟
                .expireAfterAccess(Duration.of(cacheAccessExpireTime, ChronoUnit.valueOf(cacheAccessExpireUnit)))
                // 开启metrics监控
                .recordStats()
                .build());
        return caffeineCache;
    }

    @Bean
    CompositeCache compositeCache(@Autowired RedisCache redisCacheEx, @Autowired CaffeineCache caffeineCache) {
        CompositeCache compositeCache = new CompositeCache(true, redisCacheEx, caffeineCache);
        return compositeCache;
    }


    /**
     * 恢复缓存容器水位线, 热点数据同步到redis
     */
    public void recoverCacheWaterLevel() {
//        final com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
//        // 计算缓存容器水位线
//        final int entryWaterLevel = (int) (nativeCache.estimatedSize() * cacheWaterLevelRatio);
//        // 备份需要移除并同步到redis的缓存副本
//        Map<Object, Object> entriesDup = new HashMap<>(entryWaterLevel);
//        // 批量移除缓存容器中的entry对象.
//        nativeCache.invalidateAll(nativeCache.asMap().entrySet().parallelStream()
//                // 命中率#升序排序
//                .sorted(Comparator.comparing(entry -> ( entry.getValue()).getHitsCount()))
//                .limit(entryWaterLevel)
//                .peek(entry -> entriesDup.put(entry.getKey(), entry.getValue()))
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList()));
//        // 将本地缓存副本异步同步到redis
//        redisTemplate.executorePipelined((RedisCallback<Object>) conn -> {
//            redisTemplate.opsForValue().multiSet(entriesDup);
//            return true;
//        });
    }
    @Override
    public Cache getCache(String name) {
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }
}

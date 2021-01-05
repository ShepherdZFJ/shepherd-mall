package com.shepherd.mallproduct.cache;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/4 16:45
 */
@Slf4j
public class CompositeCache extends AbstractValueAdaptingCache {

    @Resource
    private ExecutorService cacheExecutor;

    private final RedisCache redisCache;
    private final CaffeineCache caffeineCache;

    public CompositeCache(boolean allowNullValues,RedisCache redisCache, CaffeineCache caffeineCache) {
        super(allowNullValues);
        this.redisCache = redisCache;
        this.caffeineCache = caffeineCache;
    }


    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if (Objects.isNull(value)) {
            // 异步从db打数据到本地缓存
            cacheExecutor.submit(() -> doAsyncLoad(key, valueLoader));
            return null;
        }
        return (T) value;
    }

    @SneakyThrows
    <T> void doAsyncLoad(Object key, Callable<T> valueLoader) {
        put(key, valueLoader.call());
    }


    @Override
    public void put(@NonNull Object key, Object value) {
        Assert.notNull(key, "key不可为空");
        caffeineCache.put(key, value);
        redisCache.put(key, value);

    }

    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        Assert.notNull(key, "key不可为空");
        caffeineCache.putIfAbsent(key, value);
        return redisCache.putIfAbsent(key, value);
    }


    @Override
    public void evict(Object key) {
        log.info("enter#evict");
        redisCache.evict(key);
        caffeineCache.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return false;
    }

    @Override
    public void clear() {
        redisCache.clear();
        caffeineCache.clear();

    }

    @Override
    public boolean invalidate() {
        return false;
    }

    @Override
    protected Object lookup(Object key) {
        Assert.notNull(key, "key不可为空");
        ValueWrapper value = caffeineCache.get(key);
        if (Objects.nonNull(value)) {
            log.info("查询caffeine 一级缓存 key:{}, 返回值是:{}", key, value);
            return value;
        }
        value = Optional.ofNullable(redisCache.get(key)).orElse(null);
        log.info("查询redis 二级缓存 key:{}, 返回值是:{}", key, value);
        return value.get();
    }
}

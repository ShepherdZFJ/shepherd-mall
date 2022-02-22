package com.shepherd.mallproduct.cache.listener;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.shepherd.mallproduct.cache.CompositeCacheManager;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/4 20:48
 */
@Setter
@Builder
@Slf4j
public class CompositeCacheRemovalListener implements RemovalListener<Object, Object> {

    private CompositeCacheManager cacheManager;

    @Override
    public void onRemoval(@Nullable Object k, @Nullable Object v, @NonNull RemovalCause cause) {
        log.info("[移除缓存] key:{} reason:{}", k, cause.name());
        // 超出最大缓存
        if (cause == RemovalCause.SIZE) {
            cacheManager.recoverCacheWaterLevel();
        }
        // 超出过期时间
        if (cause == RemovalCause.EXPIRED) {
            // do something
        }
        // 显式移除
        if (cause == RemovalCause.EXPLICIT) {
            // do something
        }
        // 旧数据被更新
        if (cause == RemovalCause.REPLACED) {
            // do something
        }
    }
}

package com.shepherd.mallproduct.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;


/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/12/30 11:12
 */
public class CacheTest {

    public static  void test() throws InterruptedException {
        Cache<Object, Object> cache = Caffeine.newBuilder().maximumSize(1)
                .expireAfterAccess(1, TimeUnit.SECONDS).build();
        cache.put("1", 1);
        long size = cache.estimatedSize();
//        Thread.sleep(1000);
        cache.put("2", 2);
//        cache.cleanUp();
        //cache.invalidate("1");
        Object o = cache.get("1", key -> "hello");
        Object o2 = cache.get("2", key -> "hello2");
        System.out.println(o);
        System.out.println(o2);

    }

    public static void main(String[] args) throws InterruptedException {
        test();

    }
}

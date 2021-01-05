package com.shepherd.mallproduct.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/1/5 16:57
 */


@Configuration
public class RedisConfig {

    public static class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

        private final Class<T> clazz;

        public FastJsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }

            return JSON.toJSONBytes(t, SerializerFeature.WriteClassName);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            return JSON.parseObject(bytes, clazz);
        }
    }

    @Bean
    public RedisSerializer<Object> fastJsonRedisSerializer() {
        return new FastJsonRedisSerializer<>(Object.class);
    }

//    @Bean
//    public RedisProperties redisProperties() {
//        return new RedisProperties();
//    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(@Autowired RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setDatabase(redisProperties.getDatabase());
        configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

}

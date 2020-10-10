package com.shepherd.mall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 10:51
 */
public class MallBeanUtil {
    private static Logger log = LoggerFactory.getLogger(MallBeanUtil.class);

    public static <A, B> B copy(A a, Class<B> clazz) {
        if (a == null || clazz == null) {
            return null;
        }

        try {
            B b = clazz.newInstance();
            BeanUtils.copyProperties(a, b);
            return b;
        } catch (Exception e) {
            log.error("BeanUtil#copy error.", e);
        }
        return null;
    }

    public static <A, B> B copy(A a, B b) {
        if (a == null || b == null) {
            return null;
        }

        try {
            BeanUtils.copyProperties(a, b);
            return b;
        } catch (Exception e) {
            log.error("BeanUtil#copy error.", e);
        }
        return null;
    }
}

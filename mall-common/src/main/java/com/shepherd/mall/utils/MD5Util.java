package com.shepherd.mall.utils;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/7/19 0:07
 */
@Slf4j
public class MD5Util {
    /**
     * MD5加密类
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String str) {
        try {
            String s = IdUtil.objectId();
            str = str + s;
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //字符串转字节码
            byte[] strBytes = str.getBytes();
            //使用指定的字节码更新摘要
            md.update(strBytes);
            // 获得密文
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int j = 0; j < byteDigest.length; j++) {
                i = byteDigest[j];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (Exception e) {
            log.error("generate md5 error, {}", str, e);
            return null;
        }

    }

    public static void main(String[] args) {
        System.out.println(encrypt("123456"));
    }


}

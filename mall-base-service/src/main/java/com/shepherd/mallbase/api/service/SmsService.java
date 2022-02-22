package com.shepherd.mallbase.api.service;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2020/12/28 21:51
 */
public interface SmsService {

    String getSmsCode(String phoneNumber);
}

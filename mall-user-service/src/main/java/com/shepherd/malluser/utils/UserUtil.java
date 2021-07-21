package com.shepherd.malluser.utils;

import com.shepherd.mall.vo.LoginVO;
import com.shepherd.malluser.interceptor.AuthInterceptor;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/18 11:44
 */
public class UserUtil {


    public static LoginVO currentUser(){
        return AuthInterceptor.localUser.get();
    }

}

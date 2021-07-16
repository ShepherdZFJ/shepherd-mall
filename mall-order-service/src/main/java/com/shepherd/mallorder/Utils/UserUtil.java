package com.shepherd.mallorder.Utils;

import com.shepherd.mall.vo.LoginVO;
import com.shepherd.mallorder.interceptor.AuthInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/16 15:52
 */

public class UserUtil {


    public static LoginVO currentUser(){
        return AuthInterceptor.localUser.get();
    }

}

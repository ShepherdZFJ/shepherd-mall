package com.shepherd.mall.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/11/27 11:35
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ResponseResultBody {

}
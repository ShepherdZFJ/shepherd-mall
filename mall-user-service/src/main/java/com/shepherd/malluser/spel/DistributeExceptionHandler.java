package com.shepherd.malluser.spel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/16 17:52
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeExceptionHandler {
    String attachmentId();
}

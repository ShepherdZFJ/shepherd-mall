package com.shepherd.malluser.spel.aspectj;

import java.lang.annotation.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/20 18:27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {
    String success();

    String fail() default "";

    String operator() default "";

    String bizNo();

    String category() default "";

    String detail() default "";

    String condition() default "";
}
package com.shepherd.mallorder.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JobAnnotation {

    /**
     * 作业名称
     */
    String jobName() default "";

    /**
     * 分组名称
     */
    String groupName() default "";

    /**
     * 定时任务cron表达式
     */
    String cron() default "";

    /**
     * 作业回调参数
     */
    String parameter() default "";

    /**
     * 作业描述
     */
    String content() default "";

}

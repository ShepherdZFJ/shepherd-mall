package com.shepherd.mallorder.schedule;

import com.shepherd.mallorder.annotation.JobAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class JobAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private Scheduler scheduler;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)  throws BeansException {
        if(!(bean instanceof Job)){
            return bean;
        }
        try{
            Method[] methods = bean.getClass().getDeclaredMethods();
            for(Method method : methods){
                JobAnnotation t3JobAnnotation = method.getAnnotation(JobAnnotation.class);
                if(null == t3JobAnnotation){
                    continue;
                }
                //定义一个TriggerKey
                TriggerKey triggerKey = TriggerKey.triggerKey(t3JobAnnotation.jobName(), t3JobAnnotation.groupName());
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                if (null == trigger) {
                    Class<? extends Job> clazz = (Class<? extends Job>)bean.getClass();
                    //执行的job任务
                    //定义一个JobDetail,其中的定义Job类，是真正的执行逻辑所在
                    JobDetail jobDetail = JobBuilder.newJob(clazz).
                            withIdentity(t3JobAnnotation.jobName(), t3JobAnnotation.groupName()).build();
                    scheduler.scheduleJob(jobDetail, createTrigger(t3JobAnnotation));
                    log.info("Quartz 创建了job:...:{}",jobDetail.getKey());
                } else {
                    if( trigger.getCronExpression().equals(t3JobAnnotation.cron())){
                        log.info("job已存在:{}",trigger.getKey());
                    } else {
                        //更新trigger 的 cron 表达式
                        scheduler.rescheduleJob(triggerKey, createTrigger(t3JobAnnotation));
                    }
                }
            }
            scheduler.start();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o; //这里要返回o，不然启动时会报错
    }

    private Trigger createTrigger(JobAnnotation t3JobAnnotation){
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(t3JobAnnotation.cron());
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(t3JobAnnotation.jobName(), t3JobAnnotation.groupName())
                .startNow()
                .withSchedule(scheduleBuilder).build();
        return trigger;
    }
}

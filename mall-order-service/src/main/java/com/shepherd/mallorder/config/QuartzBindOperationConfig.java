//package com.shepherd.mallorder.config;
//
//
//import com.shepherd.mallorder.factory.QuartzJobFactory;
//import com.shepherd.mallorder.schedule.TestSchedule;
//import org.quartz.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// * @author fjzheng
// * @version 1.0
// * @date 2021/5/20 18:42
// */
//@Configuration
//public class QuartzBindOperationConfig {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//    @Autowired
//    private QuartzJobFactory quartzJobFactory;
//    @Autowired
//    private Scheduler scheduler;
//
//    public void scheduleBind() {
//        try {
//            scheduler.setJobFactory(quartzJobFactory);
//            JobDetail tesJobDetail = JobBuilder.newJob(TestSchedule.class)
//                    .withIdentity("tesJob", "tesJob").withDescription("定时任务demo")
//                    .build();
//            CronScheduleBuilder tesJobCronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/10 * * * * ?");
//            CronTrigger tesJobCronTrigger = TriggerBuilder.newTrigger()
//                    .withIdentity("tesJobTrigger", "tesJob")
//                    .withSchedule(tesJobCronScheduleBuilder).build();
//            scheduler.scheduleJob(tesJobDetail, tesJobCronTrigger);
//            scheduler.start();
//        } catch (SchedulerException e) {
//            // e.printStackTrace();
//        }
//
//    }
//
//}
//

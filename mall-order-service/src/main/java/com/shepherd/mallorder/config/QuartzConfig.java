//package com.shepherd.mallorder.config;
//
//import com.shepherd.mallorder.factory.QuartzJobFactory;
//import org.quartz.spi.JobFactory;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * @author fjzheng
// * @version 1.0
// * @date 2021/5/20 18:38
// */
//@Configuration
//public class QuartzConfig {
//
//    public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";
//
//    @Bean
//    public JobFactory jobFactory(ApplicationContext applicationContext) {
//        QuartzJobFactory jobFactory = new QuartzJobFactory();
//        jobFactory.setApplicationContext(applicationContext);
//        return jobFactory;
//    }
//
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource, PlatformTransactionManager transactionManager) throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setAutoStartup(true);
//        factory.setJobFactory(jobFactory);
//        factory.setQuartzProperties(quartzProperties());
//        //集群版配置
//        factory.setDataSource(dataSource);
//        factory.setTransactionManager(transactionManager);
//        return factory;
//    }
//
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES_PATH));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
//
//}

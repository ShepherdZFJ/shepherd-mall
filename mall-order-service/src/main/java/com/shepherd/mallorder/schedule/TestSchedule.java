package com.shepherd.mallorder.schedule;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/5/20 18:47
 */
import java.util.Date;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class TestSchedule implements Job{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("*****"+new Date());
    }
}

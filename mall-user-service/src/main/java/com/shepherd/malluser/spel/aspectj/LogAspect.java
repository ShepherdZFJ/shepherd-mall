package com.shepherd.malluser.spel.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/20 18:28
 */
@Component
@Aspect
public class LogAspect {

    @Pointcut("@annotation(com.shepherd.malluser.spel.aspectj.LogRecord)")
    public void logRecordPointCut() {

    }

    @Around("logRecordPointCut()")
    public Object handleLogRecord(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object target = pjp.getTarget();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        LogRecord logRecord = method.getAnnotation(LogRecord.class);
        String bizNo = logRecord.bizNo();
        String detail = logRecord.detail();
        return pjp.proceed();
    }
}

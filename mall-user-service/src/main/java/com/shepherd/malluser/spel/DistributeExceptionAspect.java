package com.shepherd.malluser.spel;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/16 17:54
 */
@Component
@Aspect
@Slf4j
public class DistributeExceptionAspect {


    private ExpressionEvaluator<String> evaluator = new ExpressionEvaluator<>();

    @Pointcut("@annotation(DistributeExceptionHandler)")
    private void exceptionHandleMethod() {

    }

    @AfterThrowing(value = "exceptionHandleMethod()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("捕获异常");
        String attachmentId = getAttachmentId(joinPoint); // 获取
        System.out.println(attachmentId);
        // 处理异常情况下的业务
    }

    private DistributeExceptionHandler getDistributeExceptionHandler(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(DistributeExceptionHandler.class);
    }

    private String getAttachmentId(JoinPoint joinPoint) {
        DistributeExceptionHandler handler = getDistributeExceptionHandler(joinPoint);
        if (joinPoint.getArgs() == null) {
            return null;
        }
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(joinPoint.getTarget(), joinPoint.getTarget().getClass(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        AnnotatedElementKey methodKey = new AnnotatedElementKey(((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getTarget().getClass());

        return evaluator.condition(handler.attachmentId(), methodKey, evaluationContext, String.class);
    }
}


package com.shepherd.malluser.spel.aspectj;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/20 18:38
 */
public class LogRecordOperationSource {
    public List<LogRecordOps> computeLogRecordOperations(@NonNull Method method, @NonNull Class<?> targetClass) {
        List<LogRecordOps> logRecordOps = new ArrayList<>();
        LogRecord logRecord = method.getAnnotation(LogRecord.class);
        if (logRecord == null) {
            return logRecordOps;
        }
        Class<? extends LogRecord> c = logRecord.getClass();
        Field[] fields = c.getDeclaredFields();
        String success = logRecord.success();
        String detail = logRecord.detail();
        String bizNo = logRecord.bizNo();
        LogRecordOps logOps = new LogRecordOps();
        if (StringUtils.isNotBlank(logRecord.bizNo())) {
            logOps.setAttr("bizNo");
            logOps.setValue(logRecord.bizNo());
        }
        if (StringUtils.isNotBlank(logRecord.success())) {
            logOps.setAttr("success");

        }
        return null;

    }

    LogRecord getLogRecord(Method method) {
        LogRecord logRecord = method.getAnnotation(LogRecord.class);
        return logRecord;
    }
}

package com.shepherd.malluser.spel;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/31 17:13
 */
public class LogRecordContext {

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> variableMapStack = new InheritableThreadLocal<>();

    private static final Map<String, Object> varMap = new ConcurrentHashMap<>();

    public static void putVariable(String key, Object value) {
        varMap.put(key, value);
    }

    public static Map<String, Object> getVariables() {
        return varMap;
    }

    //其他省略....
}

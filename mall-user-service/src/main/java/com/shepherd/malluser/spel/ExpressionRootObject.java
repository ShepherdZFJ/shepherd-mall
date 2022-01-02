package com.shepherd.malluser.spel;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/16 17:59
 */
public class ExpressionRootObject {
    private final Object object;
    private final Object[] args;

    public ExpressionRootObject(Object object, Object[] args) {
        this.object = object;
        this.args = args;
    }

    public Object getObject() {
        return object;
    }

    public Object[] getArgs() {
        return args;
    }
}

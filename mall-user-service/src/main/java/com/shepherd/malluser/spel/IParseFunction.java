package com.shepherd.malluser.spel;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/17 17:55
 */
public interface IParseFunction {

    default boolean executeBefore(){
        return false;
    }

    String functionName();

    String apply(String value);
}

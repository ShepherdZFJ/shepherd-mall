package com.shepherd.malluser.spel;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/20 17:51
 */
public interface IFunctionService {
    String apply(String functionName, String value);
    boolean beforeFunction(String functionName);
}

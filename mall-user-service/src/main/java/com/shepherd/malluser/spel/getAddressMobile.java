package com.shepherd.malluser.spel;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/31 17:38
 */

public class getAddressMobile implements IParseFunction{

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return "getAddressMobile";
    }

    @Override
    public String apply(String value) {
        return "178168759339";
    }
}

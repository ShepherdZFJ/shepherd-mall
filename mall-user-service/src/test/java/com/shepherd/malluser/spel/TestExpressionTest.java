package com.shepherd.malluser.spel;

import com.shepherd.malluser.entity.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/1/5 16:42
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestExpressionTest {
    @Resource
    private TestExpression testExpression;
    @Test
    public void test() {
        Address address = Address.builder().id(10l).province("浙江").detailAddress("余杭区未来科技城").build();
        testExpression.test(address);
    }

}
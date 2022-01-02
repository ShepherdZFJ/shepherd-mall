package com.shepherd.malluser.spel;

import com.shepherd.malluser.entity.Address;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/12/16 11:58
 */
public class TestSpEL {
    public static void main(String[] args) throws NoSuchMethodException {
//        //表达式解析器
//        ExpressionParser parser= new SpelExpressionParser() ;
//        //设置表达式
//        Expression exp = parser.parseExpression("'hello world'");
//        String str = (String) exp . getValue() ;
//        System.out .println(str) ;
//        //通过EL访问普通方法
//        exp= parser.parseExpression ( "'hello world'. charAt(0)");
//        char ch = (Character) exp . getValue () ;
//        System.out.println(ch);

        Address address = Address.builder().id(1l).city("杭州市").mobile("17816875929").build();
        ExpressionParser parser= new SpelExpressionParser();
        Expression exp = parser.parseExpression("city");
        String city = (String) exp.getValue(address);
        System.out.println(city);
        //调用getRoleName 方法
        EvaluationContext ctx = new StandardEvaluationContext(address);
        String mobile = parser.parseExpression ("getMobile()").getValue(ctx , String.class);
        System . out .println(mobile) ;

        // evaluates to a Java list containing the four numbers
        List numbers = (List) parser.parseExpression("{1,2,3,4}").getValue(ctx);
        System.out.println(numbers);
        List listOfLists = (List) parser.parseExpression("{{'a','b'},{'x','y'}}").getValue(ctx);
        System.out.println(listOfLists);

        // create an array of integers
        List<Integer> primes = new ArrayList<Integer>();
        primes.addAll(Arrays.asList(2,3,5,7,11,13,17));

        // create parser and set variable 'primes' as the array of integers
        ctx.setVariable("primes", primes);

        // all prime numbers > 10 from the list (using selection ?{...})
        // evaluates to [11, 13, 17]
        List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
                "#primes.?[#this>10]").getValue(ctx);
        System.out.println(primesGreaterThanTen);

        String greetingExp = "Hello, #{#user} ---> #{T(System).getProperty('user.home')}";
        ctx.setVariable("user", "fsx");
        Expression expression = parser.parseExpression(greetingExp, new TemplateParserContext());
        System.out.println(expression.getValue(ctx, String.class));//Hello, fsx ---> C:\Users\fangshixiang

        ctx.setVariable("alarmTime", "2018-09-26 13:00:00");
        ctx.setVariable("location", "二楼201机房");
        System.out.println(parser.parseExpression("告警发生时间 #{#alarmTime}，位置是在#{#location}", new TemplateParserContext()).getValue(ctx));


        ctx.setVariable("reverseString",
                TestSpEL.class.getDeclaredMethod("reverseString", String.class));

        String helloWorldReversed = parser.parseExpression(
                "hello, #reverseString(#user)").getValue(ctx, String.class);
        System.out.println(helloWorldReversed);


    }

    public static String reverseString(String input) {
        StringBuilder backwards = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            backwards.append(input.charAt(input.length() - 1 - i));
        }
        return backwards.toString();
    }






}

package com.lend.lendchain;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test1() {
        double d=1.0E-6;
//        BigDecimal bd2 = new BigDecimal(d);
//        String str = bd2.setScale(6, BigDecimal.ROUND_DOWN).toPlainString();
//        String s= CommonUtil.doubleFormat(d,6);
//        System.out.println(s+"");
    }
    @Test
    public void test2() {
        double d=0.45;
        int i=990;
        System.out.println("n=:"+d*i);
    }
    @Test
    public void test3() {
        double a = 1040.921999;
        double b = 1.1;
        double result = a * b;
        BigDecimal a1 = BigDecimal.valueOf(a);
        BigDecimal b1 = BigDecimal.valueOf(b);

        BigDecimal result1 = a1.multiply(b1);
        System.out.println("控制台输出：" + a + " * " + b + " = " + result);
        System.out.println("控制台输出2：" + a1 + " * " + b1 + " = " + result1);
        System.out.println("计算器正确是：" + 1145.0142);
    }
    @Test
    public void test4() {
    }

}
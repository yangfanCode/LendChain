package com.lend.lendchain.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * double 工具类 用String输出 避免科学计数法
 */
public class DoubleUtils {
    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    public static double sum(double d1,double d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1,double d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    /**
     * double 乘法
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(double d1,double d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }


    /**
     * double 除法
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double div(double d1,double d2,int scale){
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全,不会四舍五入,直接截取
     * setRoundingMode设置mode改变模式
     *
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     * @return
     */
    public static String doubleFormat(double v, int scale) {
        DecimalFormat decimalFormat;
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            decimalFormat = new DecimalFormat("0");
            return decimalFormat.format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        decimalFormat = new DecimalFormat(formatStr);
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(v);
    }

    /**
     * 将double格式化为指定小数位的double，四舍五入,不足小数位用不会不全
     *
     * @param value
     * @param scale 小数位数
     * @return BigDecimal.ROUND_HALF_UP表示四舍五入，
     * BigDecimal.ROUND_HALF_DOWN也是五舍六入，
     * BigDecimal.ROUND_UP表示进位处理（就是直接加1），
     * BigDecimal.ROUND_DOWN表示直接去掉尾数。
     */
    public static String doubleRound(double value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bd.toPlainString();
    }

    /**
     * 将double格式化为指定小数位的double，四舍五入,不足小数位自动补全
     *
     * @param value
     * @param scale
     * @return
     */
    public static String doubleRoundFormat(double value, int scale) {
        String d = doubleRound(value, scale);
        return doubleFormat(Double.valueOf(d), scale);
    }

    /**
     * double整数数无小数点,小数时有,原有double
     *
     * @param d
     * @return
     */
    public static String doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }

    /**
     * double整数无小数点,小数时有 不会四舍五入 自定义小数位数 不足小数位会补全
     *
     * @param d
     * @return
     */
    public static String doubleTransFormatTwo(double d, int scale) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return doubleFormat(d, scale);
    }
    /**
     * double整数无小数点,小数时有 不会四舍五入 自定义小数位数 最多几位小数 不足小数位不会补全
     *
     * @param d
     * @return
     */
    public static String doubleTransFormatThree(double d, int scale) {
        String valueString=doubleFormat(d, scale);
        int length=valueString.length();
        for (int i = 0; i < length; i++) {
            if (valueString.endsWith("0")) {
                valueString = valueString.substring(0, valueString.length() - 1);
            } else if (valueString.endsWith(".")) {
                valueString = valueString.substring(0, valueString.length() - 1);
                break;
            } else
                break;
        }
        return valueString;
    }

    /**
     * double整数无小数点,小数时有 四舍五入 自定义小数位数 最多几位小数 不足小数位不会补全
     *
     * @param d
     * @return
     */
    public static String doubleTransRoundTwo(double d, int scale) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(doubleRound(d, scale));
    }

    /**
     * 6位小数 ,app主要应用格式
     *
     * @param d
     * @return
     */
    public static String doubleTransRound6(double d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return doubleTransFormatThree(d, 6);
    }

    //四舍五入把double转化int整型，0.5进一，小于0.5不进一
    public static int doubleToIntRound(double number) {
        BigDecimal bd = new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }
}

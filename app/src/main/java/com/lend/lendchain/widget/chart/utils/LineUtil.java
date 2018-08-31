package com.lend.lendchain.widget.chart.utils;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * 分时线等这些图标的通用工具类
 */
public class LineUtil {

    /**
     * 传入时间和分钟如15:00，解析成分钟数，
     *
     * @param minStr 17:00
     * @return
     */
    public static int getMin(String minStr) throws NumberFormatException {
        return Integer.parseInt(minStr.split(":")[0]) * 60 + Integer.parseInt(minStr.split(":")[1]);
    }

    /**
     * 通过long时间获取分钟数
     *
     * @param time
     * @return
     */
    public static int getMin(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time * 1000);
        return getMin(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }

    /**
     * 根据开收盘的时间，计算网格竖线的x轴坐标
     *
     * @param duration
     * @return
     */
    public static float[] getXByDuration(String duration, float width) {
        if (TextUtils.isEmpty(duration)) return new float[]{};
        int showCount = getShowCount(duration);
        float xUnit = width / (float) showCount;
        ArrayList<Integer> timeMins = getTimesMin(duration);
        if (timeMins.size() == 6) {
            //如果白盘夜盘一起的话，需要画5条竖线（虚实虚实虚）
            float[] result = new float[5];
            //实线1
            result[1] = (timeMins.get(1) - timeMins.get(0)) * xUnit;
            //实线2
            result[3] = (timeMins.get(3) - timeMins.get(0) - (timeMins.get(2) - timeMins.get(1))) * xUnit;
            //虚线1
            result[0] = result[1] / 2f;
            //虚线2
            result[2] = (result[3] - result[1]) / 2f + result[1];
            //虚线3
            result[4] = (width - result[3]) / 2f + result[3];
            return result;
        } else {
            //如果只有白盘的话，需要画3条竖线（虚实虚）
            float[] result = new float[3];
            //实线1
            result[1] = (timeMins.get(1) - timeMins.get(0)) * xUnit;
            //虚线1
            result[0] = result[1] / 2f;
            //虚线2
            result[2] = (width - result[1]) / 2f + result[1];
            return result;
        }
    }

    /**
     * 判断网格是否不规则的网格：即早中晚开盘时间是否一致，不一致的话网格需要重新画
     *
     * @param duration
     * @return
     */
    public static boolean isIrregular(String duration) {
        ArrayList<Integer> timeMins = getTimesMin(duration);
        switch (timeMins.size()) {
            case 2:
                return false;
            case 4:
                return timeMins.get(3) - timeMins.get(2) != timeMins.get(1) - timeMins.get(0);
            case 6:
                return (timeMins.get(3) - timeMins.get(2) != timeMins.get(1) - timeMins.get(0)) || (timeMins.get(3) - timeMins.get(2) != timeMins.get(5) - timeMins.get(4));
        }
        return false;
    }

    /**
     * 获取开盘收盘时间对应的分钟数
     *
     * @param duration
     * @return
     */
    public static ArrayList<Integer> getTimesMin(String duration) {
        ArrayList<String> times = getTimes(duration);
        ArrayList<Integer> mins = new ArrayList<>();
        for (String s : times) {
            int min = getMin(s);
            mins.add(min);
        }
        return mins;
    }

    /**
     * 获取分时线下的时间，需要展示的点数目
     *
     * @param duration 如9:00-11:00|13:00-15:00
     * @return 上面的是4小时，返回240
     */
    public static int getShowCount(String duration) {
        ArrayList<Integer> mins = getTimesMin(duration);
        switch (mins.size()) {
            case 2:
                return mins.get(1) - mins.get(0);
            case 4:
                return (mins.get(3) - mins.get(2)) + (mins.get(1) - mins.get(0));
            case 6:
                return (mins.get(5) - mins.get(4)) + (mins.get(3) - mins.get(2)) + (mins.get(1) - mins.get(0));
        }
        return 242;
    }

    /**
     * 解析开收盘时间点
     *
     * @param duration 9:30-11:30|13:00-15:00(可能会有1、2、3段时间)
     * @return {"9:30", "11:30", "13:00", "15:00"}
     */
    public static ArrayList<String> getTimes(String duration) {
        ArrayList<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(duration)) return result;
        if (duration.contains("|")) {
            String[] t1 = duration.split("\\|");
            for (String s : t1) {
                result.add(s.split("-")[0]);
                result.add(s.split("-")[1]);
            }
        } else {
            result.add(duration.split("-")[0]);
            result.add(duration.split("-")[1]);
        }
        return result;
    }

    /**
     * 计算出Y轴显示价格的最大最小值
     *
     * @param max
     * @param min
     * @param yd
     * @return
     */
    public static double[] getMaxAndMinByYd(double max, double min, double yd) {
        double limit = Math.abs(max - yd) > Math.abs(yd - min) ? Math.abs(max - yd) : Math.abs(yd - min);
        double[] result = new double[2];
//        result[0] = yd + limit;
//        result[1] = yd - limit;
        result[0] = max;
        result[1] = min;
        return result;
    }

    /**
     * 获取该画笔写出文字的高度
     *
     * @param paint
     * @return
     */
    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * 获取该画笔写出文字的宽度
     *
     * @param paint
     * @return
     */
    public static float getTextWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * 获取数组中最大最小值
     *
     * @param list
     * @return
     */
    public static float[] getMaxAndMin(float[] list) {
        if (list == null || list.length == 0) return new float[]{0, 0};
        float max = 0, min = 0;
        float[] temp = list.clone();
        Arrays.sort(temp);
        max = temp[temp.length - 1];
        min = temp[0];
        return new float[]{max, min};
    }

    /**
     * 获取数组中最大最小值
     *
     * @param list1
     * @return
     */
    public static float[] getMaxAndMin(float[] list1, float[] list2) {
        float max = 0, min = 0;
        float[] f1 = getMaxAndMin(list1);
        float[] f2 = getMaxAndMin(list2);
//        max = f1[0] > f2[0] ? f1[0] : f2[0];
//        min = f1[1] < f2[1] ? f1[1] : f2[1];
        max = f2[0];
        min = f1[1] ;
        return new float[]{max, min};
    }

    /**
     * 获取数组中最大最小值
     *
     * @param list1
     * @return
     */
    public static float[] getMaxAndMin(float[] list1, float[] list2, float[] list3) {
        float max = 0, min = 0;
        float[] f1 = getMaxAndMin(list1);
        float[] f2 = getMaxAndMin(list2);
        float[] f3 = getMaxAndMin(list3);
        max = f1[0] > f2[0] ? f1[0] : f2[0];
        max = max > f3[0] ? max : f3[0];
        min = f1[1] < f2[1] ? f1[1] : f2[1];
        min = min < f3[1] ? min : f3[1];
        return new float[]{max, min};
    }

    /**
     * 获取数组中最大最小值
     *
     * @param list1
     * @return
     */
    public static float[] getMaxAndMin(float[] list1, float[] list2, float[] list3, float[] list4) {
        float max = 0, min = 0;
        float[] f1 = getMaxAndMin(list1);
        float[] f2 = getMaxAndMin(list2);
        float[] f3 = getMaxAndMin(list3);
        float[] f4 = getMaxAndMin(list4);
        float[] f123 = getMaxAndMin(f1, f2, f3);
        max = f123[0] > f4[0] ? f123[0] : f4[0];
        min = f123[1] < f4[1] ? f123[1] : f4[1];
        return new float[]{max, min};
    }
}

package com.lend.lendchain.utils;

import android.support.v4.content.ContextCompat;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;


/**
 * Created by yangfan on 2016/12/28.
 */
public class ColorUtils {

    /**白色背景**/
    public static int WHITE = ContextCompat.getColor(ContextHelper.getApplication(), R.color.colorWhite);
    /**透明背景**/
    public static int TRANSPARENT = ContextCompat.getColor(ContextHelper.getApplication(), android.R.color.transparent);
    /**20212A**/
    public static int COLOR_20212A = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_20212A);
    //跌颜色
    public static int COLOR_1ECC27 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_1ECC27);
    //涨颜色
    public static int COLOR_FF6343 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_FF6343);
    /**509FFF**/
    public static int COLOR_509FFF = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_509FFF);
    /**4885FF**/
    public static int COLOR_4885FF = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_509FFF);
    /**999999**/
    public static int COLOR_999999 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_999999);
    /**262626**/
    public static int COLOR_262626 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_262626);
    /**595959**/
    public static int COLOR_595959 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_595959);
    /**F5F5F5**/
    public static int COLOR_F5F5F5 = ContextCompat.getColor(ContextHelper.getApplication(), R.color.color_F5F5F5);




    /**
     * 获取价格显示的颜色，curr>change是红，等于是黑，小于是绿
     * 平是COLOR_20212A
     *
     * @param curr   当前价
     * @param change 变化颜色的价格
     * @return
     */
    public static int getTextColorAsh(double curr, double change) {
        if (curr == change)
            return COLOR_20212A;
        if (curr < change)
            return COLOR_1ECC27;
        return COLOR_FF6343;
    }

    /**
     * 获取价格显示的颜色，//0 等于 1 大于  value2>value1   -1 小于
     * 平是COLOR_20212A
     *
     * @param compareValue
     * @return
     */
    public static int getTextColorAsh(int compareValue) {
        if (compareValue == 0)
            return COLOR_20212A;
        if (compareValue == -1)
            return COLOR_1ECC27;
        return COLOR_FF6343;
    }

}

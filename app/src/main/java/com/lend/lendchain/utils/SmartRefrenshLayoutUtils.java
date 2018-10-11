package com.lend.lendchain.utils;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * SmartRefrenshLayout 属性设置
 */
public class SmartRefrenshLayoutUtils {
    private static SmartRefrenshLayoutUtils instance;
    private SmartRefrenshLayoutUtils() { }
    public static SmartRefrenshLayoutUtils getInstance() {
        if (instance == null) {
            instance = new SmartRefrenshLayoutUtils();
        }
        return instance;
    }

    /**
     * 设置SmartRefrenshLayout通用属性 动画时长等 默认越界回弹
     * @param smartRefreshLayout
     */
    public void setSmartRefrenshLayoutCommon(SmartRefreshLayout smartRefreshLayout){
        //刷新动画时间默认250比较僵硬
        smartRefreshLayout.setReboundDuration(350);
        //滑动惯性显示刷新加载等
        smartRefreshLayout.setEnableOverScrollBounce(true);
        //越界回弹
        smartRefreshLayout.setEnableOverScrollDrag(true);
        //最大滑动高度比例
        smartRefreshLayout.setHeaderMaxDragRate(1.7f);
    }
    /**
     * 设置SmartRefrenshLayout通用属性 动画时长等 无月结会谈
     * @param smartRefreshLayout
     */
    public void setSmartRefrenshLayoutCommonNoDrag(SmartRefreshLayout smartRefreshLayout){
        //刷新动画时间默认250比较僵硬
        smartRefreshLayout.setReboundDuration(350);
        //滑动惯性显示刷新加载等
        smartRefreshLayout.setEnableOverScrollBounce(true);
        //最大滑动高度比例
        smartRefreshLayout.setHeaderMaxDragRate(1.7f);
    }
}

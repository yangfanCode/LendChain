package com.lend.lendchain.widget.chart.view;


import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.chart.utils.DrawUtils;

/**
 * 图表的常量
 */
public interface ChartConstant {
    /**
     * 单击显示十字线延时，为双击全屏腾出时间
     */
    int DOUBLE_TAP_DELAY = 300;

    //K线、分时图+ 时间占SurfaceView的比例
    float MAIN_SCALE = 213f / 280f;
    //指标占比
    float INDEX_SCALE = 67f / 280f;
    //时间文字 height
    float FENSHI_TIME_HEIGHT = DrawUtils.FENSHI_TIME_SIZE + DisplayUtil.sp2px(ContextHelper.getApplication(), 3);

    //各个界面对应的下标
    int TYPE_FENSHI = 0;

    //指标对应的下标
    int INDEX_VOL = 0;

}

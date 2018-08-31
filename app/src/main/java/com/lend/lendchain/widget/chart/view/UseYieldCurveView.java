package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.chart.bean.YieldCurve;
import com.lend.lendchain.widget.chart.utils.ColorUtil;

import java.util.ArrayList;

/**
 * 收益曲线View+十字线
 */
public class UseYieldCurveView extends FrameLayout {

    protected Context mContext;
    private YieldCurveView yieldCurveView;
    private CrossView crossView;
    private TextView textView;

    public UseYieldCurveView(Context context) {
        this(context, null);
    }

    public UseYieldCurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UseYieldCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        yieldCurveView = new YieldCurveView(context);
        crossView = new CrossView(context);
        textView = new TextView(context);
        addView(yieldCurveView);
        addView(crossView);
        crossView.setVisibility(GONE);
        textView.setTextSize(12);
        textView.setTextColor(ColorUtil.COLOR_FFFFFF);
        textView.setBackgroundColor(ColorUtil.COLOR_CC20212A);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.height = DisplayUtil.dp2px(context, 20);
        textView.setPadding(DisplayUtil.dp2px(context, 12), 0, 0, 0);
        textView.setVisibility(GONE);
        textView.setMaxLines(1);
        yieldCurveView.setUsedViews(crossView, textView);
    }

    // 外部传入最大最小 值
    public void setOutYMinMax(double yMinOut, double yMaxOut) {
        yieldCurveView.setOutYMinMax(yMinOut, yMaxOut);
    }

    /**
     * 数据设置入口
     *
     * @param list
     */
    public void setDataAndInvalidate(ArrayList<YieldCurve> list) {
        yieldCurveView.setDataAndInvalidate(list);
    }

    /**
     * 加载更多数据
     *
     * @param list
     */
    public void loadMoreTimeSharingData(ArrayList<YieldCurve> list) {
        yieldCurveView.loadMoreTimeSharingData(list);
    }

    /**
     * 加载更多失败，在这里添加逻辑
     */
    public void loadMoreError() {
        yieldCurveView.loadMoreError();
    }

    /**
     * 没有更多数据，在这里添加逻辑
     */
    public void loadMoreNoData() {
        yieldCurveView.loadMoreNoData();
    }

    public void setmTimeSharingListener(ChartView.TimeSharingListener mTimeSharingListener) {
        yieldCurveView.setmTimeSharingListener(mTimeSharingListener);
    }

    public void setTimeFormat(String timeFormat) {
        yieldCurveView.setTimeFormat(timeFormat);
    }


}

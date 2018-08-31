package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.chart.bean.CMinute;
import com.lend.lendchain.widget.chart.utils.ColorUtil;

import java.util.ArrayList;

/**
 * 分时图View+十字线
 */
public class UseFenshiView extends FrameLayout {

    protected Context mContext;
    private FenshiView fenshiView;
    private CrossView crossView;
    private TextView textView;

    public UseFenshiView(Context context) {
        this(context, null);
    }

    public UseFenshiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UseFenshiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        fenshiView = new FenshiView(context);
        crossView = new CrossView(context);
        textView = new TextView(context);
        addView(fenshiView);
        addView(crossView);
        crossView.setVisibility(GONE);
        textView.setTextSize(12);
        textView.setTextColor(ColorUtil.COLOR_FFFFFF);
        textView.setBackgroundColor(ColorUtil.COLOR_CC20212A);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        addView(textView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.height = DisplayUtil.dp2px(context, 20);
        textView.setPadding(DisplayUtil.dp2px(context, 12), 0, 0, 0);
        textView.setVisibility(GONE);
        textView.setMaxLines(1);
        fenshiView.setUsedViews(crossView, textView);
    }

    // 外部传入最大最小 值
    public void setOutYMinMax(double yMinOut, double yMaxOut) {
        fenshiView.setOutYMinMax(yMinOut, yMaxOut);
    }


    /**
     * 数据设置入口
     *
     * @param list
     */
    public void setDataAndInvalidate(ArrayList<CMinute> list) {
//        fenshiView.setDataAndInvalidate(list);
    }

    /**
     * 加载更多数据
     *
     * @param list
     */
    public void loadMoreTimeSharingData(ArrayList<CMinute> list) {
//        fenshiView.loadMoreTimeSharingData(list);
    }

    /**
     * 实时推送过来的数据，实时更新
     *
     * @param cMinute
     */
    public void pushingTimeSharingData(CMinute cMinute) {
//        fenshiView.pushingTimeSharingData(cMinute);
    }

    /**
     * 加载更多失败，在这里添加逻辑
     */
    public void loadMoreError() {
        fenshiView.loadMoreError();
    }

    /**
     * 没有更多数据，在这里添加逻辑
     */
    public void loadMoreNoData() {
        fenshiView.loadMoreNoData();
    }

    public void setOnDoubleTapListener(ChartView.OnDoubleTapListener onDoubleTapListener) {
        fenshiView.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void setmTimeSharingListener(ChartView.TimeSharingListener mTimeSharingListener) {
        fenshiView.setmTimeSharingListener(mTimeSharingListener);
    }


}

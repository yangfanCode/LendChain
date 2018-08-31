package com.lend.lendchain.widget.chart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.chart.bean.StickData;
import com.lend.lendchain.widget.chart.utils.ColorUtil;

import java.util.ArrayList;

/**
 * K线View+十字线
 */
public class UseKLineView extends FrameLayout {

    protected Context mContext;
    private KLineView kLineView;
    private CrossView crossView;
    private TextView textView;

    public UseKLineView(Context context) {
        this(context, null);
    }

    public UseKLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UseKLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        kLineView = new KLineView(context);
        crossView = new CrossView(context);
        textView = new TextView(context);
        addView(kLineView);
        addView(crossView);
        crossView.setVisibility(GONE);
        textView.setTextSize(12);
        textView.setTextColor(ColorUtil.COLOR_FFFFFF);
        textView.setBackgroundColor(ColorUtil.COLOR_CC20212A);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        addView(textView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        layoutParams.height = CommonUtil.dip2px(context, 20);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(DisplayUtil.dp2px(context, 12), DisplayUtil.dp2px(context, 4), DisplayUtil.dp2px(context, 12), DisplayUtil.dp2px(context, 4));
        textView.setVisibility(GONE);
        kLineView.setUsedViews(crossView, textView);
        kLineView.setType(1);
    }

    /**
     * 数据设置入口
     *
     * @param list
     */
    public void setDataAndInvalidate(ArrayList<StickData> list) {
        kLineView.setDataAndInvalidate(list);
    }

    public void setTimeFormat(String timeFormat) {
        kLineView.setTimeFormat(timeFormat);
    }


}

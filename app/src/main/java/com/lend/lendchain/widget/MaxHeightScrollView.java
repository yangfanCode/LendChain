package com.lend.lendchain.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 最大高度scrollview
 */
public class MaxHeightScrollView extends SpringBackScrollView {

    private static final int MAX_HEIGHT = 700;
    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        child.measure(widthMeasureSpec, heightMeasureSpec);
        int width = child.getMeasuredWidth();
        int height = Math.min(child.getMeasuredHeight(), MAX_HEIGHT);
        setMeasuredDimension(width, height);
    }

}

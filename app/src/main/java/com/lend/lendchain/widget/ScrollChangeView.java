package com.lend.lendchain.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 监听ScrollView下拉刷新时滑动的距离
 */
public class ScrollChangeView extends ScrollView {
    public ScrollChangeView(Context context) {
        super(context);
    }

    public ScrollChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ScrollViewListener scrollViewListener = null;


    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy);

    }
}

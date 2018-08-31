package com.lend.lendchain.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 处理事件分发回调的frameLayout
 */
public class DispatchFrameLayout extends FrameLayout {
    private OnClick onClick;
    public void setOnClick(OnClick onClick){
        this.onClick=onClick;
    }
    public DispatchFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DispatchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onClick.onClick(this);
        return super.dispatchTouchEvent(ev);
    }

    public interface OnClick{
        void onClick(View v);
    }
}

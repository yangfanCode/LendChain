package com.lend.lendchain.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lend.lendchain.R;

/**
 * Created by yangfan on 2016/12/29.
 */
public class ViewUtils {

    /**生成中等大小的文字**/
    public static TextView generateMidTextView(Context context, String text, int color, int gravity) {
        if (context == null) return null;
        TextView textView = new TextView(context);
        textView.setGravity(gravity);
        textView.setText(text);
        textView.setTextSize(14f);
        textView.setTextColor(color);
        return textView;
    }

    /**
     * 显示或隐藏控件
     */
    public static void showViewsVisible(boolean visible, View... views) {
        if (views == null) return;
        for (View v:views) {
            try {
                v.setVisibility(visible? View.VISIBLE: View.GONE);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 显示或隐藏控件
     */
    public static void showViewsInvisible(boolean visible, View... views) {
        if (views == null) return;
        for (View v:views) {
            try {
                v.setVisibility(visible? View.VISIBLE: View.INVISIBLE);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 设置视图显示或隐藏
     * @param showView
     * @param hideViews
     */
    public static void showViewsVisible(View showView, View[] hideViews) {
        try {
            showView.setVisibility(View.VISIBLE);
            for (View view: hideViews
                 ) {
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消选中
     */
    public static void uncheckViews(ViewGroup group) {
        if (group == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof CheckBox) {
                CheckBox box = (CheckBox) v;
                box.setChecked(false);
            }
        }
    }

    /**
     * 从父节点中移除
     * @param view
     */
    public static void removeFromParent(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        parent.removeView(view);
    }

    /**
     * 设置是否可点击
     * @param isEnable
     * @param views
     */
    public static void setViewEnable(boolean isEnable,View...views){
        if (views == null) return;
        for (View v:views) {
            try {
                v.setEnabled(isEnable);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 设置textview 下划线(有vivo无效问题)
     * @param textView
     */
    public static void setTextViewBottomLine(TextView textView){
        textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
        textView.getPaint().setAntiAlias(true);//抗锯齿
    }
    /**
     * 设置textview 文字的 下划线
     * @param str
     */
    public static SpannableString setTextBottomLine(String str){
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
        return content;
    }


    /**
     * 初始化SwipeRefreshLayout的属性
     *
     * @param mSwipeLayout      控件
     * @param onrefreshlistener 事件
     * @return 返回初始化的mSwipeLayout
     */
    public static SwipeRefreshLayout initSwipeLayout(SwipeRefreshLayout mSwipeLayout, SwipeRefreshLayout.OnRefreshListener onrefreshlistener, int s) {
        mSwipeLayout.setOnRefreshListener(onrefreshlistener);
        // 设置下拉圆圈上的颜色
//        mSwipeLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3);
        mSwipeLayout.setColorSchemeResources(R.color.color_509FFF);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        if (s > 0)
            mSwipeLayout.setDistanceToTriggerSync(s);
        mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.white);// 设置下拉进度的背景颜色，默认就是白色的
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
        return mSwipeLayout;
    }

    /**
     * 设置view透明度动画
     * @param view
     * @param alphaF
     * @param alphaT
     * @param duration
     * @param delay
     */
    public static void alphaViewWithAnimation(final View view, float alphaF,float alphaT,int duration, int delay) {
        alphaViewWithAnimation(view,alphaF,alphaT,duration,delay,null,null);
    }
    /**
     * 设置view透明度动画并跳转activity 作为activity渐隐效果
     *
     * @param view               控件
     * @param duration           动画的播放时间
     * @param delay              延迟播放时间
     */
    public static void alphaViewWithAnimation(final View view,float alphaF,float alphaT, int duration, int delay,Activity activityFrom,Class<?> activityTo) {
        //创建位移动画
        AlphaAnimation ani = new AlphaAnimation(alphaF, alphaT);
        ani.setInterpolator(new AccelerateInterpolator());//设置加速器
        ani.setDuration(duration);//设置动画时间
        ani.setStartOffset(delay);//设置动画延迟时间
        //监听动画播放状态
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(activityFrom!=null&&activityTo!=null){
                    Intent intent=new Intent(activityFrom,activityTo);
                    activityFrom.startActivity(intent);
                    activityFrom.overridePendingTransition(0, 0);
                }
                view.clearAnimation();
                view.setAlpha(alphaT);
            }
        });
        view.startAnimation(ani);
    }

    /**
     * 获得view宽度
     * @param view
     * @return
     */
    public static int getViewWidth(View view){
        if(view==null)return 0;
        view.measure(0,0);
        return view.getMeasuredWidth();//获得view宽度
    }
    /**
     * 获得view高度
     * @param view
     * @return
     */
    public static int getViewHeight(View view){
        if(view==null)return 0;
        view.measure(0,0);
        return view.getMeasuredHeight();//获得view高度
    }
}

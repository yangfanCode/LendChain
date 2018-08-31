package com.lend.lendchain.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.yangfan.utils.CommonUtils;


/**
 * Created by yangfan on 2017/9/14.
 */

public class PopUtils {

    public static PopupWindow createPop(Context context, int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);//获得焦点
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setAnimationStyle(R.style.ImageBrowsePopAnimation);

        return popupWindow;
    }

    /**
     * int animationStyle
     * boolean hasBackgroundAlpha  ; true 背景半透明
     */
    public static PopupWindow createPop(Context context, int layoutId, int animationStyle, boolean hasBackgroundAlpha) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        if (animationStyle != 0)
            popupWindow.setAnimationStyle(animationStyle);
        if (hasBackgroundAlpha)
            popupWindow = SelectPicturePopupWindowUtils.initPop((Activity) context, popupWindow);

        return popupWindow;
    }

    public static PopupWindow createPop2(Context context, int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return popupWindow;
    }

    /**
     * int animationStyle
     * boolean hasBackgroundAlpha  ; true 背景半透明
     */
    public static PopupWindow createPop2(Context context, int layoutId, int animationStyle, boolean hasBackgroundAlpha) {
        return createPop2(context, layoutId, animationStyle, hasBackgroundAlpha, 0);
    }

    /**
     * int animationStyle
     * boolean hasBackgroundAlpha  ; true 背景半透明
     * int width  popupwindow 指定宽度pop
     */
    public static PopupWindow createPop2(Context context, int layoutId, int animationStyle, boolean hasBackgroundAlpha, int width) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        PopupWindow popupWindow = new PopupWindow(view, width > 0 ? width : ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        if (animationStyle != 0)
            popupWindow.setAnimationStyle(animationStyle);
        if (hasBackgroundAlpha)
            popupWindow = SelectPicturePopupWindowUtils.initPop((Activity) context, popupWindow);

        return popupWindow;
    }

    /**
     * 常用 pop （内容，取消 确定 按钮）
     */
    public static PopupWindow showNormalPop(Context context, String content, View.OnClickListener clickListener, View.OnClickListener closeClickListener) {
        return showNormalPop(context, content, clickListener, closeClickListener, 0);
    }

    /**
     * 常用 pop （内容，取消 确定 按钮）
     * int width  popupwindow 宽;  width<=0  默认屏幕宽的 65%
     */
    public static PopupWindow showNormalPop(Context context, String content, View.OnClickListener clickListener, View.OnClickListener closeClickListener, int width) {
        PopupWindow popupWindow = createPop2(context, R.layout.pop_normal_layout, 0, true, width > 0 ? width : (int) (CommonUtils.getScreenWidth(context) * 0.65));
        View view = popupWindow.getContentView();
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(content);

        view.findViewById(R.id.tv_confirm).setOnClickListener((v) -> {
            if (clickListener != null)
                clickListener.onClick(v);
            popupWindow.dismiss();
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(view1 -> {
            popupWindow.dismiss();
            if (closeClickListener != null)
                closeClickListener.onClick(view1);
        });

        return popupWindow;
    }

}

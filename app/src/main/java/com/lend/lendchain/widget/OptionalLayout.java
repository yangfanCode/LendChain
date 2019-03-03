package com.lend.lendchain.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.DisplayUtil;


/**
 * create by yangfan 2016/11/25
 * 空数据 未登录 404  500等提示页面
 */

public class OptionalLayout extends LinearLayout {
    private TextView tvStatus, tvLogin;
    private ImageView imvStatus;

    private TypeEnum typeEnum = TypeEnum.NO_DATA;

    public OptionalLayout(Context context) {
        this(context, null);
    }

    public OptionalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        imvStatus = new ImageView(context);
        imvStatus.setImageResource(R.mipmap.icon_no_data);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = DisplayUtil.dp2px(context,49f);
        addView(imvStatus, layoutParams);
        tvStatus = new TextView(context);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int pad15 = DisplayUtil.dp2px(context,15f);
        layoutParams.leftMargin = pad15;
        layoutParams.rightMargin = pad15;
        layoutParams.topMargin = DisplayUtil.dp2px(context,29f);
        layoutParams.bottomMargin = DisplayUtil.dp2px(context,150f);
        tvStatus.setGravity(Gravity.CENTER);
        tvStatus.setTextColor(ColorUtils.COLOR_595959);
        tvStatus.setText(R.string.no_data);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f);
        addView(tvStatus, layoutParams);
        tvLogin = new TextView(context);
        tvLogin.setText(R.string.no_login);
        tvLogin.setGravity(Gravity.CENTER);
        tvLogin.setTextColor(ColorUtils.COLOR_262626);
        tvLogin.setBackgroundResource(R.drawable.nologinfor_optional_bg);
        int pad4 = DisplayUtil.dp2px(context,4f);
        int pad45 = DisplayUtil.dp2px(context,45f);
        tvLogin.setPadding(pad45, pad4, pad45, pad4);
        addView(tvLogin, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setTypeEnum(typeEnum);
    }

    public void setTextToTvStatus(int textId) {
        tvStatus.setText(textId);
    }

    public void setTextToTvLogin(int textId) {
        tvLogin.setText(textId);
    }

    public void setTextNoBackground() {
        tvLogin.setBackground(null);
    }

    public TextView getTvStatus() {
        return tvStatus;
    }

    public TextView getTvLogin() {
        return tvLogin;
    }

    public ImageView getImvStatus() {
        return imvStatus;
    }

    public void setTextTvLoginVisibility(int visibility){
        tvLogin.setVisibility(visibility);
    }

    public void setBtnOnClickListener(View.OnClickListener l) {
        tvLogin.setOnClickListener(l);
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        setContent(typeEnum);
    }

    public void setBackBround(int color){
        setBackgroundColor(color);
    }

    private void setContent(TypeEnum typeEnum) {
        if (typeEnum == null) return;
        this.typeEnum = typeEnum;

        switch (typeEnum) {
//            case NOT_LOGIN:
//                getImvStatus().setImageResource(R.mipmap.gz_rocket);
//                setTextToTvStatus(R.string.no_subscribe_login);
//                setTextToTvLogin(R.string.btn_go_login);
//                break;
            case NO_DATA:
                getImvStatus().setImageResource(R.mipmap.icon_no_data);
                setTextToTvStatus(R.string.no_data);
                setTextTvLoginVisibility(GONE);
                break;
        }
    }

    public enum TypeEnum {
//        NOT_LOGIN("未登录"),
        NO_DATA("无数据");

        private String typeDesc;


        TypeEnum(String typeDesc) {
            this.typeDesc = typeDesc;
        }
    }
}
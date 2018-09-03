package com.lend.lendchain.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.activity.account.MyWalletActivity;
import com.lend.lendchain.utils.CommonUtil;


/**
 * Created by js on 2018/1/18.
 * 需要充值面板
 */
public class PayNeedReChargePopWindow {
    private PopupWindow popupWindow;
    private Activity activity;
    private double amount;//余额
    private double income;//收益
    private double amountInput;//输入的金额
    private String code;
    public PayNeedReChargePopWindow(Activity activity,double amount,String code,double income,double amountInput) {
        this.activity = activity;
        this.amount=amount;
        this.code=code;
        this.income=income;
        this.amountInput=amountInput;
    }
    public PayNeedReChargePopWindow(Activity activity,double amount,String code,double amountInput) {
        this.activity = activity;
        this.amount=amount;
        this.code=code;
        this.amountInput=amountInput;
    }

    public PopupWindow creatPop() {
        View contentView = LayoutInflater.from(activity)
                .inflate(R.layout.pay_need_recharge_layout, null);
        ImageView ivClose=contentView.findViewById(R.id.pay_need_recharge_ivClose);
        TextView tvOver=contentView.findViewById(R.id.pay_need_recharge_tvOver);
        TextView tvOverP=contentView.findViewById(R.id.pay_need_recharge_tvOverP);
        TextView tvIncome=contentView.findViewById(R.id.pay_need_recharge_tvIncome);
        TextView btnGoRecharge=contentView.findViewById(R.id.pay_need_recharge_btnGoRecharge);
        LinearLayout llIncome=contentView.findViewById(R.id.pay_need_recharge_llIcome);
        tvOver.setText(CommonUtil.doubleTransRound6(amount)+" "+code);
        tvOverP.setText(activity.getString(R.string.over_to_use).concat(CommonUtil.doubleTransRound6(amount)+""));
        llIncome.setVisibility(income==0?View.GONE:View.VISIBLE);
        if(llIncome.getVisibility()==View.VISIBLE)tvIncome.setText(CommonUtil.doubleTransRound6(income)+" "+code);//到期收益
        btnGoRecharge.setText(activity.getString(R.string.over_lack_please_recharge).concat(CommonUtil.doubleTransRound6(amountInput-amount)+" "+code));
        btnGoRecharge.setOnClickListener(v -> {
            dismiss();
            CommonUtil.openActicity(activity, MyWalletActivity.class,null);
        });
        ivClose.setOnClickListener(v -> dismiss());
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,true
        );
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.popWindow_animation);
        popupWindow.setOnDismissListener(() -> {
            CommonUtil.setBackgroundAlpha(activity,1.0f);
        });
        return popupWindow;
    }
    private void dismiss(){
        if(popupWindow!=null)popupWindow.dismiss();
    }
}

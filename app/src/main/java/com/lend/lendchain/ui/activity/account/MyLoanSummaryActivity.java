package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyLoanSummary;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.invest.InvestSummaryActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.widget.CustomDialog;
import com.yangfan.widget.FormNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class MyLoanSummaryActivity extends BaseActivity {
    @BindView(R.id.my_loan_summary_fnCodeStatus)
    FormNormal fnCodeStatus;
    @BindView(R.id.my_loan_summary_fnLoanAmount)
    FormNormal fnLoanAmount;
    @BindView(R.id.my_loan_summary_fnInterst)
    FormNormal fnInterst;
    @BindView(R.id.my_loan_summary_fnDeadLine)
    FormNormal fnDeadLine;
    @BindView(R.id.my_loan_summary_fnSendTime)
    FormNormal fnSendTime;
    @BindView(R.id.my_loan_summary_fnFulledStandard)
    FormNormal fnFulledStandard;
    @BindView(R.id.my_loan_summary_fnRepayTime)
    FormNormal fnRepayTime;
    @BindView(R.id.my_loan_summary_fnMortage)
    FormNormal fnMortage;
    @BindView(R.id.my_loan_summary_fnRaisePercent)
    FormNormal fnRaisePercent;
    @BindView(R.id.my_loan_summary_btnGoRepay)
    TextView btnGoRepay;
    @BindView(R.id.my_loan_summary_btnAddMortage)
    TextView btnAddMortage;
    @BindView(R.id.my_loan_summary_layoutGoRepay)
    FrameLayout layoutGoRepay;
    @BindView(R.id.my_loan_summary_layoutAddMortage)
    FrameLayout layoutAddMortage;
    @BindView(R.id.my_loan_summary_llBtn)
    LinearLayout llBtn;
    @BindView(R.id.loan_summary_llParent)
    LinearLayout llParent;
    @BindView(R.id.my_loan_summary_tvGotoSummary)
    TextView tvSummary;
    private String borrowId,interest,deadline,quotaFullTime,realPaybackTime,status;
    private MyLoanSummary summary=null;
    private int position;//还款列表pos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loan_summary);
        StatusBarUtil.setStatusBarColor(MyLoanSummaryActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(MyLoanSummaryActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.loan_summary));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        borrowId=getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
        deadline=getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        quotaFullTime=getIntent().getExtras().getString(Constant.ARGS_PARAM2);
        realPaybackTime=getIntent().getExtras().getString(Constant.ARGS_PARAM3);
        status=getIntent().getExtras().getString(Constant.ARGS_PARAM4);
        interest=getIntent().getExtras().getString(Constant.ARGS_PARAM5);
        position=getIntent().getExtras().getInt(Constant.ARGS_PARAM6);
        initData();
        initListener();
    }

    private void initData() {
        NetApi.myLoanSummary(this,borrowId,myLoanSummaryObserver);
    }

    private void initListener() {
        btnAddMortage.setOnClickListener(v -> {
            if(summary==null)return;
            Bundle bundle=new Bundle();
            bundle.putString(Constant.ARGS_PARAM1,summary.borrowCryptoId);
            bundle.putString(Constant.ARGS_PARAM2,summary.mortgageCryptoId);
            bundle.putString(Constant.ARGS_PARAM3,summary.borrowCryptoCode);
            bundle.putString(Constant.ARGS_PARAM4,summary.mortgageCryptoCode);
            bundle.putDouble(Constant.ARGS_PARAM5,summary.mortgageAmount);
            bundle.putDouble(Constant.ARGS_PARAM6,summary.borrowAmount);
            bundle.putString(Constant.ARGS_PARAM7,summary.id);
            CommonUtil.openActicity(MyLoanSummaryActivity.this,LoanAddActivity.class,bundle);
        });
        btnGoRepay.setOnClickListener(v -> {
            CustomDialog.Builder builder=new CustomDialog.Builder(MyLoanSummaryActivity.this);
            builder.setTitle(getString(R.string.warm_tips));
            if((Long.valueOf(deadline)*1000)<System.currentTimeMillis()){
                builder.setMessage(getString(R.string.confirm_within_repay));
                status="13";
            }else if((Long.valueOf(deadline)*1000)==System.currentTimeMillis()){
                builder.setMessage(getString(R.string.confirm_nomal_repay));
                status="12";
            }else{
                builder.setMessage(getString(R.string.confirm_before_repay));
                status="11";
            }
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                NetApi.payBack(MyLoanSummaryActivity.this,true, SPUtil.getToken(),borrowId,payBackObserver);
                dialog.dismiss();
            });
            Dialog dialog=builder.create();
            builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
            builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
            dialog.show();
            dialog.setCancelable(false);
        });
        tvSummary.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            //传标的id
            bundle.putString(Constant.INTENT_EXTRA_DATA, borrowId);
            CommonUtil.openActicity(MyLoanSummaryActivity.this, InvestSummaryActivity.class, bundle);
        });
    }
    private void setData() {
        //1 两个按钮  0隐藏去还款
        if(getString(R.string.wait_repay).equals(status)){
            llBtn.setVisibility(View.VISIBLE);
            layoutGoRepay.setVisibility(View.VISIBLE);
            layoutAddMortage.setVisibility(View.VISIBLE);
        }else if(getString(R.string.raiseing).equals(status)){
            layoutGoRepay.setVisibility(View.GONE);
            layoutAddMortage.setVisibility(View.VISIBLE);
            llBtn.setVisibility(View.VISIBLE);
        }else{
            llBtn.setVisibility(View.GONE);
        }
        fnCodeStatus.setTitle(summary.orderId);//标name
        fnCodeStatus.setText(status);//状态
        fnLoanAmount.setText(DoubleUtils.doubleTransRound6(summary.borrowAmount)+" "+summary.borrowCryptoCode);//借入金额
        fnInterst.setText(interest);//利息
        fnDeadLine.setText(!TextUtils.isEmpty(deadline)?TimeUtils.getDateToStringS(Long.parseLong(deadline),TimeUtils.YYYY_MM_dd_HH_MM_SS1):"");//到期时间
        fnSendTime.setText(!TextUtils.isEmpty(summary.createTime)? TimeUtils.getDateToStringS(Long.parseLong(summary.createTime),TimeUtils.YYYY_MM_dd_HH_MM_SS1):"");//发标时间
        fnFulledStandard.setText(quotaFullTime);//满标时间
        fnRepayTime.setText(realPaybackTime);//还款时间
        fnMortage.setText(DoubleUtils.doubleTransRound6(summary.mortgageAmount)+" "+summary.mortgageCryptoCode);//抵押物
        int progress=DoubleUtils.doubleToIntRound( (summary.boughtAmount / summary.borrowAmount * 100));//进度
        fnRaisePercent.setText(DoubleUtils.doubleTransRoundTwo(progress, 2) + "%");//募集百分比
        ViewUtils.showViewsVisible(true,llParent);//显示父布局
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    Observer<ResultBean<MyLoanSummary>> myLoanSummaryObserver=new NetClient.RxObserver<ResultBean<MyLoanSummary>>() {
        @Override
        public void onSuccess(ResultBean<MyLoanSummary> myLoanSummaryResultBean) {
            if(myLoanSummaryResultBean==null)return;
            if(myLoanSummaryResultBean.isSuccess()){
                if(myLoanSummaryResultBean.data!=null){
                    summary=myLoanSummaryResultBean.data;
                    setData();
                }
            }else{
                TipsToast.showTips(myLoanSummaryResultBean.message);
            }
        }
    };

    Observer<ResultBean>payBackObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.repay_success));
                //成功后隐藏去还款
                layoutGoRepay.setVisibility(View.GONE);
                layoutAddMortage.setVisibility(View.VISIBLE);
                llBtn.setVisibility(View.VISIBLE);
                Bundle bundle=new Bundle();
                bundle.putInt(Constant.INTENT_EXTRA_DATA,position);
                bundle.putString(Constant.ARGS_PARAM1,status);
                CommonUtil.openActicity(MyLoanSummaryActivity.this,MyLoanActivity.class,bundle);
            }else{
                setHttpFailed(MyLoanSummaryActivity.this,resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };



}

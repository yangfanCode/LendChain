package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.KeyBoardInputPopWindow;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.DecimalDigitsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 追加借款
 */
public class LoanAddActivity extends BaseActivity {
    @BindView(R.id.loan_add_tvMortageCoinPrice)
    TextView tvMortageCoinPrice;
    @BindView(R.id.loan_add_tvMortageCount)
    TextView tvMortageCount;
    @BindView(R.id.loan_add_tvMortageSufficientRate)
    TextView tvMortageSufficientRate;
    @BindView(R.id.loan_add_tvAddCode)
    TextView tvAddCode;
    @BindView(R.id.loan_add_tvAddRate)
    TextView tvAddRate;
    @BindView(R.id.loan_add_tvGoRecharge)
    TextView tvGoRecharge;
    @BindView(R.id.loan_add_tvMortagePrize)
    TextView tvMortagePrize;
    @BindView(R.id.loan_add_tvWarnLine)
    TextView tvWarnLine;
    @BindView(R.id.loan_add_tvCloseLine)
    TextView tvCloseLine;
    @BindView(R.id.loan_add_etAddCount)
    DecimalDigitsEditText etAddCount;
    @BindView(R.id.loan_add_btnConfirm)
    Button btnConfirm;
    @BindView(R.id.load_add_llParent)
    LinearLayout llParent;
    private String borrowCryptoId,borrowCryptoCode,mortgageCryptoCode, mortgageCryptoId,borrowId;
    private double mortgageAmount,borrowAmount;
    private double price;
    private PopupWindow popupWindow = null;
    private KeyBoardInputPopWindow keyBoardInputPopWindow = null;
    private String mortageSufficientRate;//抵押物充足率

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_add);
        StatusBarUtil.setStatusBarColor(LoanAddActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(LoanAddActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.additional));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        borrowCryptoId=getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        mortgageCryptoId=getIntent().getExtras().getString(Constant.ARGS_PARAM2);
        borrowCryptoCode=getIntent().getExtras().getString(Constant.ARGS_PARAM3);
        mortgageCryptoCode=getIntent().getExtras().getString(Constant.ARGS_PARAM4);
        mortgageAmount=getIntent().getExtras().getDouble(Constant.ARGS_PARAM5);
        borrowAmount=getIntent().getExtras().getDouble(Constant.ARGS_PARAM6);
        borrowId=getIntent().getExtras().getString(Constant.ARGS_PARAM7);
        initData();
        initListener();
    }

    private void initData() {
        NetApi.getCoinPrice(this, true,mortgageCryptoId,borrowCryptoId, getCoinPriceObserver);
    }


    private void initListener() {
        etAddCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String input=s.toString().trim();
                if(!TextUtils.isEmpty(input)&&!input.endsWith(".")){
                    double rio=price*(mortgageAmount+Double.parseDouble(input))/borrowAmount;
                    tvAddRate.setText(DoubleUtils.doubleTransFormatTwo(rio*100,2)+"%");
                }else{
                    if(TextUtils.isEmpty(input)){//展示默认
                        tvAddRate.setText(mortageSufficientRate+"%");
                    }
                }
            }
        });
        btnConfirm.setOnClickListener(v -> {
            String input=etAddCount.getText().toString().trim();
            if(TextUtils.isEmpty(input)){
                TipsToast.showTips(getString(R.string.please_input_add_counts));
                return;
            }
            //读取用户此币种余额 是否不足
            NetApi.getCoinCount(LoanAddActivity.this,false, SPUtil.getToken(),Integer.valueOf(mortgageCryptoId), getCoinCountObserver);

        });
        tvGoRecharge.setOnClickListener(v -> CommonUtil.openActicity(LoanAddActivity.this,MyWalletActivity.class,null));
    }

    Observer<ResultBean<SimpleBean>> getCoinPriceObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    price = resultBean.data.price;
                    double warning=DoubleUtils.mul(borrowAmount,1.4);//警戒线数值
                    double closing=DoubleUtils.mul(borrowAmount,1.1);//平仓线数值
                    double mortage=DoubleUtils.mul(mortgageAmount,price);//抵押价值
                    if(Double.valueOf(DoubleUtils.doubleTransRound6(price))==0){//数值太小 6位显示0的时候取元数据
                        tvMortageCoinPrice.setText("1 "+mortgageCryptoCode+" ≈ "+ DoubleUtils.getFormatDouble(price)+" "+borrowCryptoCode);//抵押币种市价
                    }else{
                        tvMortageCoinPrice.setText("1 "+mortgageCryptoCode+" ≈ "+ DoubleUtils.doubleTransRound6(price)+" "+borrowCryptoCode);//抵押币种市价
                    }
                    tvMortageCount.setText(DoubleUtils.doubleTransRound6(mortgageAmount)+" "+mortgageCryptoCode);//抵押数量
                    tvMortagePrize.setText(DoubleUtils.doubleTransRound6(mortage)+" "+borrowCryptoCode);//抵押价值
                    tvWarnLine.setText(DoubleUtils.doubleTransRound6(warning)+" "+borrowCryptoCode+"(140%)");//警戒线
                    tvCloseLine.setText(DoubleUtils.doubleTransRound6(closing)+" "+borrowCryptoCode+"(110%)");//平仓线
                    tvAddCode.setText(mortgageCryptoCode);//追加抵押code
                    double leastAdd=(warning-mortage)/price;
                    etAddCount.setHint(getString(R.string.least_add).concat(DoubleUtils.doubleTransRound6(leastAdd>0?leastAdd:0)).concat(" "+mortgageCryptoCode));
                    if(borrowAmount!=0){
                        double rio=price*mortgageAmount/borrowAmount;
                        mortageSufficientRate=DoubleUtils.doubleTransRoundTwo(rio*100,2);
                        if(rio<1.4){//低于警戒线
                            tvMortageSufficientRate.setText(mortageSufficientRate+"%"+getString(R.string.below_the_security_line));
                        }else{
                            tvMortageSufficientRate.setText(mortageSufficientRate+"%");
                        }
                        tvAddRate.setText(mortageSufficientRate+"%");
                        ViewUtils.showViewsVisible(true,llParent,btnConfirm);//父布局显示
                    }
                }
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean> addMortageObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.addition_success));
                finish();
            }else{
                setHttpFailed(LoanAddActivity.this,resultBean);
            }
            keyBoardInputPopWindow.dismiss();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean<SimpleBean>> getCoinCountObserver=new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null){
                    String count=etAddCount.getText().toString().trim();
                    double over=resultBean.data.amount;
                    if(Double.parseDouble(count)>over){
                        TipsToast.showTips(getString(R.string.over_lack));
                    }else{
                        if (popupWindow == null) {//展示余额弹窗
                            if (keyBoardInputPopWindow == null) {
                                keyBoardInputPopWindow = new KeyBoardInputPopWindow(LoanAddActivity.this,2);
                                keyBoardInputPopWindow.setOnOkClick(() -> {
                                    String amount=etAddCount.getText().toString().trim();
                                    String google=keyBoardInputPopWindow.getEditTextGoogleCode().getText().toString().trim();
                                    NetApi.addMortgage(LoanAddActivity.this,SPUtil.getToken(),borrowId,google,amount,addMortageObserver);
                                });
                                keyBoardInputPopWindow.setOnCancelClick(() -> {
//                                    TipsToast.showTips("cancle");
                                });
                            }
                            popupWindow = keyBoardInputPopWindow.creatPop();
                        }
                        CommonUtil.setBackgroundAlpha(LoanAddActivity.this, 0.5f);
                        popupWindow.showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);

                    }
                }
            }else{
                setHttpFailed(LoanAddActivity.this,resultBean);
            }
        }
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };


}

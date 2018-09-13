package com.lend.lendchain.ui.activity.invest;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.APPCommonNavigatorAdapter;
import com.lend.lendchain.bean.InvestSummary;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.SafeCertifyActivity;
import com.lend.lendchain.ui.activity.account.UserCenterActivity;
import com.lend.lendchain.ui.fragment.invest.AddRecordFragment;
import com.lend.lendchain.ui.fragment.invest.InvestRecordFragment;
import com.lend.lendchain.ui.fragment.invest.InvestSummaryFragment;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.KeyBoardInputPopWindow;
import com.lend.lendchain.widget.MortgageFullProgressView;
import com.lend.lendchain.widget.PayNeedReChargePopWindow;
import com.lend.lendchain.widget.TipsToast;
import com.umeng.analytics.MobclickAgent;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.CustomDialog;
import com.yangfan.widget.CustomFragmentPagerAdapter;
import com.yangfan.widget.CustomViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 投资详情
 * 1 抵押标 2 预告 3平台标 4新手预告 5新手标
 */

public class InvestSummaryActivity extends BaseActivity {

    @BindView(R.id.invest_summary_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.invest_summary_tvExpectAnnualized)
    TextView tvExpectAnnualized;
    @BindView(R.id.invest_summary_tvPeriod)
    TextView tvPeriod;
    @BindView(R.id.invest_summary_tvBorrowAmount)
    TextView tvBorrowAmount;
    @BindView(R.id.invest_summary_tvMortgageAsset)
    TextView tvMortgageAsset;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.item_invest_vpSummary)
    CustomViewPager customViewPager;
    @BindView(R.id.invest_summary_pb)
    ProgressBar progressBar;
    @BindView(R.id.item_invest_tvPercent)
    TextView tvPercent;
    @BindView(R.id.invest_summary_tvInvestLave)
    TextView tvInvestLave;
    @BindView(R.id.invest_summary_tvStartTime)
    TextView tvStartTime;
    @BindView(R.id.invest_summary_tvPurchaseFunds)
    TextView tvPurchaseFunds;
    @BindView(R.id.invest_summary_tvMortgagePrice)
    TextView tvMortgagePrice;
    @BindView(R.id.invest_summary_mfView)
    MortgageFullProgressView mfView;
    @BindView(R.id.invest_summary_layoutPb)
    LinearLayout layoutPb;
    private String borrowId;
    private PopupWindow popupWindow = null;
    private KeyBoardInputPopWindow keyBoardInputPopWindow = null;
    private String code, nickName, investLave;
    private double interestRates, mortgageAmount, borrowAmount;
    private int borrowDays;
    private Dialog dialog = null;
    private int borrowCryptoId;//币种id
    private int borrowTypeId;//标的type类型
    private CountDownTimer timer;
    private double newUserAmount;//新手剩余额度
    private double minInvestAmount;//起购资金

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;//设置布局顶到状态栏
        setContentView(R.layout.activity_invest_summary);
        StatusBarUtil.StatusBarDarkMode(InvestSummaryActivity.this);//状态栏图标白色
        int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0, height, 0, 0);//设置是状态栏渐变色
//        StatusBarUtil.setStatusBarColor(InvestSummaryActivity.this,R.drawable.bg_4885ff_gradient_509fff);//状态栏颜色
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        borrowId = getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
        baseTitleBar.getTvTitle().setTextColor(ColorUtils.WHITE);
        baseTitleBar.getTvTitleChild().setTextColor(ColorUtils.WHITE);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        tvMortgagePrice.setVisibility(View.GONE);
        initData();
        initListener();
    }

    private void initData() {
        NetApi.investSummary(this, true, borrowId, summaryObserver);
        if (SPUtil.isLogin())
            NetApi.getUserInfo(this, false, SPUtil.getToken(), userInfoObserver);//请求用户信息
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InvestSummaryActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InvestSummaryActivity");
    }

    private void initListener() {
        btnConfirm.setOnClickListener(v -> {
            //友盟埋点 立即投资
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.INVEST);
            if (CommonUtil.isLoginElseGotoLogin(InvestSummaryActivity.this)) {//没登录自动跳转登录
                if (SPUtil.getUserPhone() && SPUtil.getUserGoogle()) {//如果手机 和谷歌都认证
                    if (!TextUtils.isEmpty(nickName)) {
                        NetApi.getCoinCount(InvestSummaryActivity.this, true, SPUtil.getToken(), borrowCryptoId, getCoinCountObserver);//读取用户此币种余额
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(InvestSummaryActivity.this);
                        builder.setTitle(getString(R.string.warm_tips));
                        builder.setMessage(getString(R.string.please_set_nickname));
                        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                        builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            CommonUtil.openActicity(InvestSummaryActivity.this, UserCenterActivity.class, null);
                            dialog.dismiss();
                        });
                        Dialog dialogNick = builder.create();
                        builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
                        builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
                        dialogNick.setCancelable(false);
                        dialogNick.show();
                    }
                } else {
                    if (dialog == null) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(InvestSummaryActivity.this);
                        builder.setTitle(getString(R.string.warm_tips));
                        builder.setMessage(getString(R.string.please_goto_certified));
                        builder.setNegativeButton(getString(R.string.look_again), (dialog, which) -> dialog.dismiss());
                        builder.setPositiveButton(getString(R.string.go_certified), (dialog, which) -> {
                            CommonUtil.openActicity(InvestSummaryActivity.this, SafeCertifyActivity.class, null);
                            dialog.dismiss();
                        });
                        dialog = builder.create();
                        builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
                        builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
                        dialog.setCancelable(false);
                    }
                    dialog.show();
                }
            }
        });
    }

    Observer<ResultBean<InvestSummary>> summaryObserver = new NetClient.RxObserver<ResultBean<InvestSummary>>() {
        @Override
        public void onSuccess(ResultBean<InvestSummary> investSummaryResultBean) {
            if (investSummaryResultBean == null) return;
            if (investSummaryResultBean.isSuccess()) {
                if (investSummaryResultBean.data != null) {
                    borrowTypeId = investSummaryResultBean.data.borrowTypeId;
                    setDefaultViewState();//设置布局 默认显示 内容
                    borrowCryptoId = investSummaryResultBean.data.borrowCryptoId;
                    mortgageAmount = investSummaryResultBean.data.mortgageAmount;
                    borrowAmount = investSummaryResultBean.data.borrowAmount;
                    if (SPUtil.isLogin() && (borrowTypeId == 4 || borrowTypeId == 5)) {//如果登录了请求新手剩余额度接口 未登录使用详情接口的minAmount
                        NetApi.getCoinCount(InvestSummaryActivity.this, false, SPUtil.getToken(), borrowCryptoId, new NetClient.RxObserver<ResultBean<SimpleBean>>() {
                            @Override
                            public void onSuccess(ResultBean<SimpleBean> resultBean) {
                                newUserAmount = resultBean.data.lastNewAmount;
                                tvMortgageAsset.setText(DoubleUtils.doubleTransRound6(newUserAmount) + " " + investSummaryResultBean.data.borrowCryptoCode);
                            }
                        });//读取用户此币种余额
                    }
                    setData(investSummaryResultBean.data);
                }
            } else {
                setHttpFailed(InvestSummaryActivity.this, investSummaryResultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    //设置布局 默认显示 内容
    private void setDefaultViewState() {
        btnConfirm.setEnabled(!(borrowTypeId == 2 || borrowTypeId == 4));//设置默认状态 预告为false
        //设置2个格额还是3个格 平台标两个格
        if (borrowTypeId == 2 || borrowTypeId == 3) {//平台标 不展示
            ViewUtils.showViewsVisible(false, findViewById(R.id.invest_summary_viewDivider3), findViewById(R.id.invest_summary_llMortgageAsset));
        } else {
            ViewUtils.showViewsVisible(true, findViewById(R.id.invest_summary_viewDivider3), findViewById(R.id.invest_summary_llMortgageAsset));
            TextView tvBorrowAmountText = findViewById(R.id.invest_summary_tvBorrowAmountText);
            if (borrowTypeId == 1) {//抵押标展示抵押额度
                tvBorrowAmountText.setText(getString(R.string.mortgage_asset));
                tvMortgagePrice.setVisibility(View.VISIBLE);
            } else {//新手标 展示剩余新手额度
                tvBorrowAmountText.setText(getString(R.string.novice_over_amount));
            }
        }
    }

    Observer<ResultBean<SimpleBean>> getCoinCountObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    double amount = resultBean.data.amount;//余额
                    if (popupWindow == null) {//展示余额弹窗
                        if (keyBoardInputPopWindow == null) {
                            keyBoardInputPopWindow = new KeyBoardInputPopWindow(InvestSummaryActivity.this, 1, code, interestRates, borrowDays, amount, investLave);
                            keyBoardInputPopWindow.setOnOkClick(() -> {
                                int inputType = keyBoardInputPopWindow.getInputType();
                                if (inputType == 1) {//输入金额模式点确定
                                    String amountStr = keyBoardInputPopWindow.getEditTextMoney().getText().toString().trim();
                                    if (TextUtils.isEmpty(amountStr)) {
                                        TipsToast.showTips(getString(R.string.please_input_invest_amount));
                                        return;
                                    }
                                    double amountInput = Double.parseDouble(amountStr);
                                    if (amountInput < minInvestAmount) {//小于起购
                                        if (!amountStr.equals(investLave)) {//当余额小于最小起购 排除余额全投
                                            TipsToast.showTips(getString(R.string.input_must_greater_minAmount) + "(" + getString(R.string.min) + DoubleUtils.doubleTransRound6(minInvestAmount) + ").");//最小起购提示
                                            return;
                                        }
                                        //此时允许余额全投
                                    }
                                    if (borrowTypeId == 4 || borrowTypeId == 5) {
                                        if (amountInput > newUserAmount) {//大于新手剩余额度
                                            TipsToast.showTips(getString(R.string.new_user_not_enough));
                                            return;
                                        }
                                    }
                                    //友盟埋点 确认投资
                                    UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.INVEST_CONFIRM);
                                    if (amount < amountInput) {//余额不足 展示余额页面
                                        popupWindow.dismiss();
                                        double income = keyBoardInputPopWindow.getIncome();
                                        PayNeedReChargePopWindow payNeedReChargePopWindow = new PayNeedReChargePopWindow(InvestSummaryActivity.this, amount, code, income, amountInput);
                                        CommonUtil.setBackgroundAlpha(InvestSummaryActivity.this, 0.5f);
                                        payNeedReChargePopWindow.creatPop().showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);
                                    } else {//余额够用 展示谷歌验证码
                                        keyBoardInputPopWindow.setInputGoogleCodeMode();
                                    }
                                } else {//输入谷歌验证码模式点确定投资提交
                                    if (!CommonUtils.isFastDoubleClick(0.5)) {
                                        String amountStr = keyBoardInputPopWindow.getEditTextMoney().getText().toString().trim();
                                        String googleCode = keyBoardInputPopWindow.getEditTextGoogleCode().getText().toString().trim();
                                        if (!TextUtils.isEmpty(googleCode)) {
                                            //友盟埋点 提交谷歌验证码
                                            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.INVEST_SUBMIT_GOOGLECODE);
//                                        keyBoardInputPopWindow.setKeyEnabled(false);//点击之后不可点击
                                            NetApi.investCreat(InvestSummaryActivity.this, SPUtil.getToken(), borrowId, amountStr, googleCode, investCreateObserver);
                                        }
                                    }
                                }
                            });
                            keyBoardInputPopWindow.setOnCancelClick(() -> {
                            });
                        }
                        popupWindow = keyBoardInputPopWindow.creatPop();
                    }
                    keyBoardInputPopWindow.setInputMoneyMode();//默认输入金额
                    CommonUtil.setBackgroundAlpha(InvestSummaryActivity.this, 0.5f);
                    popupWindow.showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);
                }
            } else {
                setHttpFailed(InvestSummaryActivity.this, resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean> investCreateObserver = new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                //友盟埋点 投资成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.INVEST_SUCCESSFUL);
                TipsToast.showTips(getString(R.string.invest_success));
                finish();
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            //键盘可以点击
//            keyBoardInputPopWindow.setKeyEnabled(true);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            //键盘可以点击
//            keyBoardInputPopWindow.setKeyEnabled(true);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
    //保存用户认证信息
    Observer<ResultBean<UserInfo>> userInfoObserver = new NetClient.RxObserver<ResultBean<UserInfo>>() {
        @Override
        public void onSuccess(ResultBean<UserInfo> userInfoResultBean) {
            if (userInfoResultBean == null) return;
            if (userInfoResultBean.isSuccess()) {
                nickName = userInfoResultBean.data.nickname;//昵称
                SPUtil.setUserPhone(userInfoResultBean.data.identif.phone);//存手机认证状态
                SPUtil.setUserGoogle(userInfoResultBean.data.identif.google);//存谷歌认证状态
                SPUtil.setUserKyc(userInfoResultBean.data.identif.kyc);//存kyc认证状态
            } else {
                setHttpFailed(InvestSummaryActivity.this, userInfoResultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
    //计算抵押物市价
    Observer<ResultBean<SimpleBean>> priceObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> simpleBeanResultBean) {
            if (simpleBeanResultBean == null) return;
            if (simpleBeanResultBean.isSuccess()) {
                double price = simpleBeanResultBean.data.price;
                if (mfView.getVisibility() == View.VISIBLE) {
                    double rio = price * mortgageAmount / borrowAmount;
                    mfView.setProgress((int) (rio * 100));//设置警戒线进度条数据
                }
                tvMortgagePrice.setText(getString(R.string.mortgage_price) + "  ".concat(DoubleUtils.doubleTransRound6(price * mortgageAmount)).concat(" " + code));//抵押物市价
            }
        }
    };

    //    1 抵押标 2 预告 3平台标 4新手预告 5新手标
    private void setData(InvestSummary investSummary) {
        investLave = DoubleUtils.doubleTransRound6(DoubleUtils.sub(borrowAmount, investSummary.boughtAmount));
        //设置进度条布局
        int status = investSummary.status;
        if (borrowTypeId == 1) {//抵押标
            if (status > 0) {//抵押标满标展示警戒线布局
                mfView.setVisibility(View.VISIBLE);
                layoutPb.setVisibility(View.GONE);
                mfView.setPoint1Text("(" + DoubleUtils.doubleTransRound6(borrowAmount * 1.1).concat(" " + investSummary.borrowCryptoCode) + ")");
                mfView.setPoint2Text("(" + DoubleUtils.doubleTransRound6(borrowAmount * 1.4).concat(" " + investSummary.borrowCryptoCode) + ")");
            } else {
                nomalProgress(investSummary);
            }
            //请求 抵押物资产 价格
            NetApi.getCoinPrice(InvestSummaryActivity.this, false, String.valueOf(investSummary.mortgageCryptoId), String.valueOf(borrowCryptoId), priceObserver);
            btnConfirm.setEnabled(status == 0);//按钮  设置是否可点击 除了0都不能点
            //抵押标第三个格数据
            tvMortgageAsset.setText(DoubleUtils.doubleTransRound6(mortgageAmount).concat(" " + investSummary.mortgageCryptoCode));
        } else if (borrowTypeId == 2) {//平台标预告
            countDownTime(investSummary.startTime * 1000);//按钮 预告显示倒计时
            nomalProgress(investSummary);
        } else if (borrowTypeId == 3) {//3平台标
            btnConfirm.setEnabled(status == 0);//按钮  设置是否可点击 除了0都不能点
            nomalProgress(investSummary);
        } else if (borrowTypeId == 4) {//4新手预告
            countDownTime(investSummary.startTime * 1000);//按钮  预告显示倒计时
            newUser3thData(investSummary);
            nomalProgress(investSummary);
        } else if (borrowTypeId == 5) {//5新手标
            btnConfirm.setEnabled(status == 0);//按钮  设置是否可点击 除了0都不能点
            newUser3thData(investSummary);
            nomalProgress(investSummary);
        }
        code = investSummary.borrowCryptoCode;//币名称
        interestRates = investSummary.interestRates;
        borrowDays = investSummary.borrowDays;
        baseTitleBar.setTitle(investSummary.orderId);
        if (!TextUtils.isEmpty(investSummary.nickname))
            baseTitleBar.setTitleChild("(" + investSummary.nickname + ")");
        //创建viewpager
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        if (borrowTypeId == 1) {//抵押标特殊处理 详情改为须知
            adapter.addFrag(InvestSummaryFragment.newInstance(getString(R.string.invest_guidelines_text)), getString(R.string.invest_guidelines));
            adapter.addFrag(InvestRecordFragment.newInstance(investSummary.investList, code), getString(R.string.purchased_record));
            if (borrowTypeId == 1 && status > 0) {//追加记录页面
                adapter.addFrag(AddRecordFragment.newInstance(investSummary.mortgateAddList, investSummary.mortgageCryptoCode), getString(R.string.add_record));
            }
        } else {
            adapter.addFrag(InvestSummaryFragment.newInstance(investSummary.desc), getString(R.string.invest_summary));
            adapter.addFrag(InvestRecordFragment.newInstance(investSummary.investList, code), getString(R.string.purchased_record));
        }
        //创建tab
        customViewPager.setAdapter(adapter);
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdjustMode(true);
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(this);
        colorTransitionPagerTitleView.setTextSize(11f);
        navigator.setAdapter(new APPCommonNavigatorAdapter(adapter.getTitles(), customViewPager,colorTransitionPagerTitleView));
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, customViewPager);
        tvExpectAnnualized.setText(DoubleUtils.doubleRoundFormat(interestRates * 360 * 100, 2));
        tvPeriod.setText(borrowDays + getString(R.string.day));//周期
        tvBorrowAmount.setText(DoubleUtils.doubleTransRound6(borrowAmount) + " " + investSummary.borrowCryptoCode);//借入资产
        tvStartTime.setText(TimeUtils.getDateToStringS(borrowTypeId == 1 ? investSummary.createTime : investSummary.startTime, TimeUtils.YYYY_MM_dd_HH_MM_SS1));//开始时间
        minInvestAmount = investSummary.minInvestAmount;
        tvPurchaseFunds.setText(DoubleUtils.doubleTransRound6(minInvestAmount) + " " + investSummary.borrowCryptoCode);//起购资金
    }

    //新手标第三个格数据
    private void newUser3thData(InvestSummary investSummary) {
        if (!SPUtil.isLogin()) {//如果未登录取minAmount数据作为新手剩余额度 新手标 第3个格数据
            newUserAmount = investSummary.minAmount;
            tvMortgageAsset.setText(DoubleUtils.doubleTransRound6(newUserAmount) + " " + investSummary.borrowCryptoCode);
        }
    }

    /**
     * 展示普通进度条布局 除了抵押标满标
     *
     * @param investSummary
     */
    private void nomalProgress(InvestSummary investSummary) {
        int progress = DoubleUtils.doubleToIntRound((investSummary.boughtAmount / borrowAmount * 100));//进度
        mfView.setVisibility(View.GONE);
        layoutPb.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);//进度条
        tvPercent.setText(DoubleUtils.doubleTransRoundTwo(investSummary.boughtAmount / borrowAmount * 100, 2) + "%");//百分比
        tvInvestLave.setText(investLave + " " + investSummary.borrowCryptoCode);//剩余可投
    }


    //倒计时
    private void countDownTime(long startTime) {
        btnConfirm.setEnabled(false);
        if (startTime < System.currentTimeMillis()) {
            btnConfirm.setEnabled(true);
            return;
        }
        long timeFuture = startTime - System.currentTimeMillis();//获取与打开计时器时的时间差 初始化计时器
        timer = new CountDownTimer(timeFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long nowTime = System.currentTimeMillis();//每次获取当前时间
                btnConfirm.setText(TimeUtils.getDistanceTime(nowTime, startTime));
            }

            @Override
            public void onFinish() {
                btnConfirm.setEnabled(true);
                btnConfirm.setText(getString(R.string.investNow));
                if (timer != null) {
                    timer.cancel();//置空
                    timer = null;//计时结束释放资源,防止对个对象计时不准
                }
            }
        };
        timer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();//置空
            timer = null;//计时结束释放资源,防止对个对象计时不准
        }
    }
}

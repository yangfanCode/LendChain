package com.lend.lendchain.ui.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.NetConst;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.account.MyInvestActivity;
import com.lend.lendchain.ui.activity.account.MyLoanActivity;
import com.lend.lendchain.ui.activity.account.MyMessageActivity;
import com.lend.lendchain.ui.activity.account.MyWalletActivity;
import com.lend.lendchain.ui.activity.account.rechargewithdraw.RechangeWithdrawRecordActivity;
import com.lend.lendchain.ui.activity.account.certify.SafeCertifyActivity;
import com.lend.lendchain.ui.activity.account.UserCenterActivity;
import com.lend.lendchain.ui.activity.common.AboutUsActivity;
import com.lend.lendchain.ui.activity.common.HelpCenterActivity;
import com.lend.lendchain.ui.activity.common.SettingActivity;
import com.lend.lendchain.ui.activity.common.WebActivity;
import com.lend.lendchain.ui.activity.login.LoginActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yangfan.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 我的fragment
 */
public class MineFragment extends BaseFragment {
    private String tag = "MineFragment";
    @BindView(R.id.my_wallet_tvEmail)
    TextView tvEmail;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.mine_login_exit_layout)
    LinearLayout llLoginExit;
    @BindView(R.id.mine_login_layout)
    LinearLayout llLogin;
    @BindView(R.id.mine_tvMyWallet)
    TextView tvMyWallet;
    @BindView(R.id.mine_tvMyInvest)
    TextView tvMyInvest;
    @BindView(R.id.mine_tvRechargrRecord)
    TextView tvRechargrRecord;
    @BindView(R.id.mine_tvMyLoan)
    TextView tvMyLoan;
    @BindView(R.id.mine_tvUserCenter)
    TextView tvUserCenter;
    @BindView(R.id.mine_tvSafeCenter)
    TextView tvSafeCenter;
    @BindView(R.id.mine_tvSupportGift)
    TextView tvSupportGift;
    @BindView(R.id.mine_tvAboutUs)
    TextView tvAboutUs;
    @BindView(R.id.mine_tvHelpCenter)
    TextView tvHelpCenter;
    @BindView(R.id.mine_tvSetting)
    TextView tvSetting;
    @BindView(R.id.mine_tvTotalMoney)
    TextView tvTotalMoney;
    @BindView(R.id.mine_tvPeriod)
    TextView tvPeriod;
    @BindView(R.id.mine_tvOver)
    TextView tvOver;
    @BindView(R.id.mine_tvLogin)
    TextView tvLogin;
    @BindView(R.id.mine_ivEye)
    ImageView ivEye;
    @BindView(R.id.mine_llOver)
    LinearLayout llOver;
    @BindView(R.id.mine_ivMessage)
    ImageView ivMessage;
    @BindView(R.id.mine_viewRedPoint)
    View viewRedPoint;
    private double totalAmount;//总资产
    private double lastAmount;//可用余额
    private double profit;//昨日收益
    private View parentView;
    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_mine, container,
                    false);
            initView();
            initData(false);//第一次进入刷新
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    //每次都刷新数据
    @Override
    public void onResume() {
        super.onResume();
        //友盟页面统计混乱修复
//        if(getUserVisibleHint()){
//            onVisibilityChangedToUser(true, tag);
//        }
        //每次都刷新数据
        if (isFirst&&SPUtil.isLogin()) {
            setRefrensh();//第一次登录刷新动画展示
            isFirst = false;
        } else {
            initData(false);//以后没有动画
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //友盟页面统计混乱修复
//        onVisibilityChangedToUser(false, tag);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isResumed()) {
//            onVisibilityChangedToUser(isVisibleToUser, tag);
//        }
//    }

    @Override
    protected void onVisible() {
    }


    private void initView() {
        ButterKnife.bind(this, parentView);
        refreshLayout.setEnableLoadMore(false);
        //设置 我的页面 沉浸式刷新
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()).setAccentColor(ColorUtils.WHITE).setFinishDuration(0).setTextTimeMarginTop(3f));
        //初始化SmartRefrenshLayout属性
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutCommonNoDrag(refreshLayout);
        initListener();
    }

    private void initData(boolean isShow) {
        scrollView.fullScroll(ScrollView.FOCUS_UP);//滑到顶部
        //改变登录ui状态
        boolean isLogin = SPUtil.isLogin();
        llLogin.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        llLoginExit.setVisibility(isLogin ? View.GONE : View.VISIBLE);
        if (llLogin.getVisibility() == View.VISIBLE) {//如果登录
            int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
            llLogin.setPadding(0, height, 0, 0);
            ViewUtils.showViewsVisible(false, tvPeriod, tvOver, tvTotalMoney);//先隐藏掉
            //返回刷新数据
            NetApi.getUserInfo(getActivity(), isShow, SPUtil.getToken(), userInfoObserver);
        } else {
            refreshLayout.finishRefresh();
        }
    }

    //返回自动刷新
    public void setRefrensh() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);//滑到顶部
        refreshLayout.autoRefresh();
    }

    private void initListener() {
        ivMessage.setOnClickListener(v -> CommonUtils.openActicity(getActivity(), MyMessageActivity.class,null));
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
        ivEye.setOnClickListener(v -> {
            if (SPUtil.getUserTotalAmountGone()) {//隐藏状态时 需要显示
                //总资产文字富文本
                SpannableString spannableString = new SpannableString("≈" + DoubleUtils.doubleTransRound6(totalAmount));
                //再构造一个改变字体大小的Span
                spannableString.setSpan(new AbsoluteSizeSpan(20, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTotalMoney.setText(spannableString);
                tvPeriod.setText("≈" + DoubleUtils.doubleTransRound6(profit));
                tvOver.setText("≈" + DoubleUtils.doubleTransRound6(lastAmount));

                ivEye.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mine_eye_open));
            } else {//显示状态时  需要隐藏
                tvTotalMoney.setText("****");
                tvPeriod.setText("****");
                tvOver.setText("****");
                ivEye.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mine_eye_close));
            }
            SPUtil.setUserTotalAmountGone(!SPUtil.getUserTotalAmountGone());
        });
        tvLogin.setOnClickListener(v -> {
            //友盟埋点 注册登录
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_LOGIN_SIGNUP);
            CommonUtil.openActivityForResult(getActivity(), MineFragment.this, LoginActivity.class, Constant.REQUEST_CODE1);
        });
        tvMyWallet.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), MyWalletActivity.class, null));
        tvMyInvest.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), MyInvestActivity.class, null));
        tvRechargrRecord.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), RechangeWithdrawRecordActivity.class, null));
        tvMyLoan.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), MyLoanActivity.class, null));
        tvSafeCenter.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), SafeCertifyActivity.class, null));
        tvAboutUs.setOnClickListener(v -> CommonUtil.openActicity(getActivity(), AboutUsActivity.class, null));
        tvHelpCenter.setOnClickListener(v -> CommonUtil.openActicity(getActivity(), HelpCenterActivity.class, null));
        tvUserCenter.setOnClickListener(v -> CommonUtil.openActivityWithLogin(getActivity(), UserCenterActivity.class, null));
        tvSupportGift.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5() + NetConst.SUPPORT_GIFT + LanguageUtils.getUserLanguageSetting());
            bundle.putInt(Constant.ARGS_PARAM1, 1);
            CommonUtil.openActivityWithLogin(getActivity(), WebActivity.class, bundle);
        });
        tvSetting.setOnClickListener(v -> CommonUtil.openActivityForResult(getActivity(), MineFragment.this, SettingActivity.class, Constant.REQUEST_CODE1));
        llOver.setOnClickListener(v -> CommonUtil.openActicity(getActivity(), MyWalletActivity.class, null));
    }

    //保存用户认证信息
    Observer<ResultBean<UserInfo>> userInfoObserver = new NetClient.RxObserver<ResultBean<UserInfo>>() {
        @Override
        public void onSuccess(ResultBean<UserInfo> userInfoResultBean) {
            if (userInfoResultBean == null) return;
            if (userInfoResultBean.isSuccess()) {
                viewRedPoint.setVisibility((int)userInfoResultBean.data.noticeAmount==0?View.GONE:View.VISIBLE);//小红点
                tvEmail.setText(TextUtils.isEmpty(userInfoResultBean.data.nickname) ? userInfoResultBean.data.email : userInfoResultBean.data.nickname);//用户名
                SPUtil.setUserPhone(userInfoResultBean.data.identif.phone);//存手机认证状态
                SPUtil.setUserGoogle(userInfoResultBean.data.identif.google);//存谷歌认证状态
                SPUtil.setUserKyc(userInfoResultBean.data.identif.kyc);//存kyc认证状态
                totalAmount = userInfoResultBean.data.totalAmount;
                lastAmount = userInfoResultBean.data.lastAmount;
                profit = userInfoResultBean.data.profit;
                //总资产文字富文本
                SpannableString spannableString = new SpannableString("≈" + DoubleUtils.doubleTransRound6(totalAmount));
                //再构造一个改变字体大小的Span
                spannableString.setSpan(new AbsoluteSizeSpan(20, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTotalMoney.setText(SPUtil.getUserTotalAmountGone() ? "****" : spannableString);
                tvPeriod.setText(SPUtil.getUserTotalAmountGone() ? "****" : "≈" + DoubleUtils.doubleTransRound6(profit));
                tvOver.setText(SPUtil.getUserTotalAmountGone() ? "****" : "≈" + DoubleUtils.doubleTransRound6(lastAmount));
                ivEye.setImageBitmap(BitmapFactory.decodeResource(getResources(), SPUtil.getUserTotalAmountGone() ? R.mipmap.icon_mine_eye_close : R.mipmap.icon_mine_eye_open));
                ViewUtils.showViewsVisible(true, tvPeriod, tvOver, tvTotalMoney);
            } else {
                if ("2002".equals(userInfoResultBean.code)) {//退出登录
                    SPUtil.setLoginExit();
                    llLogin.setVisibility(View.GONE);
                    llLoginExit.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            refreshLayout.finishRefresh();
            TipsToast.showTips(getString(R.string.netWorkError));
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            refreshLayout.finishRefresh();
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    //登录或者退出登录登录返回刷新数据
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constant.REQUEST_CODE1 && (resultCode == Constant.RESULT_CODE1||resultCode == Constant.RESULT_CODE2)) {
//            setRefrensh();
//        }
//    }
}

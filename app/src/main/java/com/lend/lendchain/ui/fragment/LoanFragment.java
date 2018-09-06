package com.lend.lendchain.ui.fragment;


import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.LoanPairs;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.MyLoanActivity;
import com.lend.lendchain.ui.activity.account.SafeCertifyActivity;
import com.lend.lendchain.ui.activity.account.UserCenterActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.KeyBordUtils;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.BaseTitleBar;
import com.lend.lendchain.widget.KeyBoardInputPopWindow;
import com.lend.lendchain.widget.PayNeedReChargePopWindow;
import com.lend.lendchain.widget.TipsToast;
import com.lvfq.pickerview.OptionsPickerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.CustomDialog;
import com.yangfan.widget.DecimalDigitsEditText;
import com.yangfan.widget.FormNormal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 借款fragment
 */
public class LoanFragment extends Fragment {
    @BindView(R.id.base_title_bar)
    BaseTitleBar baseTitleBar;
    @BindView(R.id.loan_fnLoanType)
    FormNormal fnLoanType;
    @BindView(R.id.loan_fnMortgageeCoin)
    FormNormal fnMortgageeCoin;
    @BindView(R.id.loan_fnLoanCoin)
    FormNormal fnLoanCoin;
    @BindView(R.id.loan_fnLoanDeadLine)
    FormNormal fnLoanDeadLine;
    @BindView(R.id.loan_fnLoanDeadLinePay)
    FormNormal fnLoanDeadLinePay;
    @BindView(R.id.loan_etMortgageCount)
    DecimalDigitsEditText etMortgageCount;
    @BindView(R.id.loan_etLoanCount)
    DecimalDigitsEditText etLoanCount;
    @BindView(R.id.loan_mortgageCount_layout)
    FrameLayout mortgageCount_layout;
    @BindView(R.id.loan_loanCount_layout)
    FrameLayout loanCount_layout;
    @BindView(R.id.loan_tvSeekBarMin)
    TextView tvSeekBarMin;
    @BindView(R.id.loan_tvSeekBarMax)
    TextView tvSeekBarMax;
    @BindView(R.id.loan_tvSeekBarRate)
    TextView tvSeekBarRate;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loan_seekBar)
    SeekBar seekBar;
    @BindView(R.id.loan_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.loan_cbLoanRequire)
    CheckBox cbLoanRequire;
    @BindView(R.id.loan_tvLoanRequire)
    TextView tvLoanRequire;
    private double price;//币种交易价格
    private double mortgateRate;//抵押率;
    private View parentView;
    private List<LoanPairs> listPairTotal;//未去重总数据
    //抵押类型
    private ArrayList<String> listLoanTypeText;//抵押类型文字picker
    private String loanType;//picker选择的
    //抵押币种
    private Set<String> listMortgageeCoinText;//去重 抵押币种文字picker
    private String mortgageCoin;//picker选择的
    //借入币种
    private Set<String> listLoanCoinText;//去重 借入币种文字picker
    private String loanCoin;//picker选择的
    //币与id的集合
    private Map<String, String> coinId;//存币的id
    //借款期限
    private ArrayList<String> listLoadDeadLine;//借款期限文字picker
    private String loadDeadLine;//picker选择的
    private double min_usdt_rate = 0.0005;//usdt的默认年最低利率
    private double min_other_rate = 0.0003;//其他的默认年最低利率
    private double max_other_rate = 0.01;//其他的默认年最高利率
    private PopupWindow popupWindow = null;
    private KeyBoardInputPopWindow keyBoardInputPopWindow = null;
    private Dialog dialog = null;
    private long timestamp;//时间戳
    private String symbol, nickName;//牌价对标识
    private boolean isFirst = false;
    private double minBorrowAmount;//最小借入限制
    private Dialog dialogLoan=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_loan, container,
                    false);
            int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
            parentView.setPadding(0, height, 0, 0);
            initView();
            initListener();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setRefrensh();
    }

    //返回自动刷新
    public void setRefrensh() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);//滑到顶部
        refreshLayout.autoRefresh(100);
    }

    private void initView() {
        ButterKnife.bind(this, parentView);
        baseTitleBar.setTitle(getString(R.string.i_want_loan));
        baseTitleBar.getImvLeftBack().setVisibility(View.GONE);
        setSeekBarEnabled(false);
        refreshLayout.setEnableLoadMore(false);
        etLoanCount.clearFocus();
        etMortgageCount.clearFocus();
        listPairTotal = new ArrayList<>();
        listLoanTypeText = new ArrayList<>();
        listMortgageeCoinText = new LinkedHashSet<>();
        listLoanCoinText = new LinkedHashSet<>();
        coinId = new HashMap<>();
        listLoadDeadLine = new ArrayList<>();
        listLoadDeadLine.add("3".concat(getString(R.string.day_ri)));
        listLoadDeadLine.add("7".concat(getString(R.string.day_ri)));
        listLoadDeadLine.add("14".concat(getString(R.string.day_ri)));
        listLoadDeadLine.add("30".concat(getString(R.string.day_ri)));
        listLoadDeadLine.add("90".concat(getString(R.string.day_ri)));
        loadDeadLine = listLoadDeadLine.get(0);
        fnLoanDeadLine.setText(loadDeadLine);
        tvSeekBarMax.setText(DoubleUtils.doubleRoundFormat(max_other_rate * 100, 2) + "%");//最高利率
    }

    private void initData(boolean isShow) {
        setDefaultState();//默认状态
        NetApi.loanTypes(getActivity(), loanTypeObserver);
        NetClient.getInstance().getPost("", isShow, getActivity()).loanPairs().subscribeOn(Schedulers.io())
                .flatMap(listResultBean -> {
                    clearPairs();//清除数据
                    listPairTotal.addAll(listResultBean.data);
                    int size = listPairTotal.size();
                    //抵押币种数据
                    for (int i = 0; i < size; i++) {
                        listMortgageeCoinText.add(listPairTotal.get(i).mortgageRyptoCode);
                        listLoanCoinText.add(listPairTotal.get(i).borrowCryptoCode);
                        coinId.put(listPairTotal.get(i).mortgageRyptoCode, listPairTotal.get(i).mortgageCryptoId);//存币种数据
                    }
                    //设置默认值
                    mortgageCoin = listPairTotal.get(0).mortgageRyptoCode;
                    loanCoin = listPairTotal.get(0).borrowCryptoCode;
                    getMortgateRate(loanCoin, mortgageCoin);//选择完交易对后获得抵押率
                    Map<String, Object> map = new HashMap<>();
                    map.put("mortgageCryptoId", coinId.get(mortgageCoin));
                    map.put("borrowCryptoId", coinId.get(loanCoin));
                    return NetClient.getInstance().getPost("", false, getActivity()).getCoinPrice(map);

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getCoinPriceObserver);
        NetApi.getUserInfo(getActivity(), false, SPUtil.getToken(), userInfoObserver);//请求用户信息
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
        etLoanCount.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(getActivity(), etLoanCount));
        etMortgageCount.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(getActivity(), etMortgageCount));
        //借款类型
        fnLoanType.setOnClickListener(v -> {
            OptionsPickerView optionsPickerView = new OptionsPickerView(getActivity());
            optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    loanType = listLoanTypeText.get(options1);
                    fnLoanType.setText(loanType);
                }
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(listLoanTypeText);
            optionsPickerView.setSelectOptions(listLoanTypeText.indexOf(loanType));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        //抵押币种
        fnMortgageeCoin.setOnClickListener(v -> {
            ArrayList<String> list = calcMortgageData();//处理集合
            OptionsPickerView optionsPickerView = new OptionsPickerView(getActivity());
            optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                mortgageCoin = list.get(options1);
                fnMortgageeCoin.setText(mortgageCoin);
                NetApi.getCoinPrice(getActivity(), true, coinId.get(mortgageCoin), coinId.get(loanCoin), getCoinPriceObserver);
                setDefaultState();//默认状态
                getMortgateRate(loanCoin, mortgageCoin);//选择完交易对后获得抵押率
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(list);
            optionsPickerView.setSelectOptions(list.indexOf(mortgageCoin));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        //借入币种
        fnLoanCoin.setOnClickListener(v -> {
            ArrayList<String> list = calcLoanData();//处理集合
            OptionsPickerView optionsPickerView = new OptionsPickerView(getActivity());
            optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                loanCoin = list.get(options1);
                fnLoanCoin.setText(loanCoin);
                NetApi.getCoinPrice(getActivity(), true, coinId.get(mortgageCoin), coinId.get(loanCoin), getCoinPriceObserver);
                setDefaultState();//默认状态
                getMortgateRate(loanCoin, mortgageCoin);//选择完交易对后获得抵押率
                setSeekBarMinMaxRate(loanCoin);//设置默认利率
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(list);
            optionsPickerView.setSelectOptions(list.indexOf(loanCoin));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        //edittext获取焦点
        loanCount_layout.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(getActivity(), etLoanCount));
        mortgageCount_layout.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(getActivity(), etMortgageCount));
        etMortgageCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void afterTextChanged(Editable s) {
                if (etMortgageCount.isFocused()) {
                    String input = s.toString().trim();
                    if (!TextUtils.isEmpty(input) && !input.endsWith(".")) {
                        //科学计数法
                        etLoanCount.setText(DoubleUtils.doubleTransRound6(Double.valueOf(input) * mortgateRate * price));
                        calcLoanDeadLineInterest();//计算展示利息
                    } else {
                        if(TextUtils.isEmpty(input)){
                            etLoanCount.getText().clear();
                            fnLoanDeadLinePay.setText("0 " + loanCoin);//借入利息
                        }
                    }
                }
            }
        });
        etLoanCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void afterTextChanged(Editable s) {
                if (etLoanCount.isFocused()) {
                    String input = s.toString().trim();
                    if (!TextUtils.isEmpty(input) && !input.endsWith(".")) {
                        //科学计数法
                        etMortgageCount.setText(DoubleUtils.doubleTransRound6(Double.valueOf(input) / mortgateRate / price));
                        calcLoanDeadLineInterest();//计算展示利息
                    } else {
                        if(TextUtils.isEmpty(input)){
                            etMortgageCount.getText().clear();
                            fnLoanDeadLinePay.setText("0 " + loanCoin);//借入利息
                        }
                    }
                }
            }
        });
        //借款期限
        fnLoanDeadLine.setOnClickListener(v -> {
            OptionsPickerView optionsPickerView = new OptionsPickerView(getActivity());
            optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    loadDeadLine = listLoadDeadLine.get(options1);
                    fnLoanDeadLine.setText(loadDeadLine);
                    calcLoanDeadLineInterest();//计算展示利息
                }
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(listLoadDeadLine);
            optionsPickerView.setSelectOptions(listLoadDeadLine.indexOf(loadDeadLine));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        //进度条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double minrate = getMinRate();
                LogUtils.LogE(LoanFragment.class, minrate + "");
                tvSeekBarRate.setText(DoubleUtils.doubleRoundFormat((getMinRate() + (max_other_rate - getMinRate()) / 100 * progress) * 100, 2) + "%");
                calcLoanDeadLineInterest();//计算展示利息
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btnConfirm.setOnClickListener(v -> {
            //借款需要全部认证
            if (CommonUtil.isLoginElseGotoLogin(getActivity())) {//登录
                if (SPUtil.getUserPhone() && SPUtil.getUserGoogle() && SPUtil.getUserKyc() == 2) {//认证
                    if (!TextUtils.isEmpty(nickName)) {
                        if(!cbLoanRequire.isChecked()){//借款须知
                            TipsToast.showTips(getString(R.string.please_read_borrowing_requirements));
                            return;
                        }
                        String mortgageCount=etMortgageCount.getText().toString().trim();
                        String loanCount=etLoanCount.getText().toString().trim();
                        if(TextUtils.isEmpty(mortgageCount)){
                            TipsToast.showTips(getString(R.string.please_input_mortgage_count));
                            return;
                        }
                        if(TextUtils.isEmpty(loanCount)){
                            TipsToast.showTips(getString(R.string.please_input_loan_count));
                            return;
                        }
                        double loanC=Double.parseDouble(loanCount);
                        if(loanC<minBorrowAmount){
                            TipsToast.showTips(getString(R.string.minimum_number_is)+DoubleUtils.doubleTransRound6(minBorrowAmount));
                            return;
                        }
                        //读取用户此币种余额 是否不足
                        NetApi.getCoinCount(getActivity(),false, SPUtil.getToken(), Integer.valueOf(coinId.get(mortgageCoin)), getCoinCountObserver);
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        builder.setTitle(getString(R.string.warm_tips));
                        builder.setMessage(getString(R.string.please_set_nickname));
                        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                        builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            CommonUtil.openActicity(getActivity(), UserCenterActivity.class, null);
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
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        builder.setTitle(getString(R.string.warm_tips));
                        builder.setMessage(getString(R.string.please_goto_certified_all));
                        builder.setNegativeButton(getString(R.string.look_again), (dialog, which) -> dialog.dismiss());
                        builder.setPositiveButton(getString(R.string.go_certified), (dialog, which) -> {
                            CommonUtil.openActicity(getActivity(), SafeCertifyActivity.class, null);
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
        //借款须知
        tvLoanRequire.setOnClickListener(v -> {
            if(dialogLoan==null){
                CustomDialog.Builder builder=new CustomDialog.Builder(getActivity());
                View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loan_requirements,null);
                ImageView ivClose=view.findViewById(R.id.dialog_loan_requirement_ivClose);
                ivClose.setOnClickListener(v1 ->dialogLoan.dismiss() );
                builder.setContentView(view);
                builder.setScreenRatioX(0.9);
                dialogLoan=builder.create();
            }
            dialogLoan.show();
        });
    }

    private void initCreateLoanData() {
        //请求最新价格
        NetApi.getCoinPrice(getActivity(), false, coinId.get(mortgageCoin), coinId.get(loanCoin), new NetClient.RxObserver<ResultBean<SimpleBean>>() {
            @Override
            public void onSuccess(ResultBean<SimpleBean> resultBean) {
                if (resultBean == null) return;
                if (resultBean.isSuccess()) {
                    if (resultBean.data != null) {
                        minBorrowAmount=resultBean.data.minBorrowAmount;
                        price = resultBean.data.price;
                        timestamp = resultBean.data.timestamp;//时间戳
                        symbol = resultBean.data.symbol;//牌价对标识
                        String mortgageCountInput = etMortgageCount.getText().toString().trim();//抵押数量
                        double newLoanCount = Double.valueOf(mortgageCountInput) * mortgateRate * price;//最新可借入的数量
                        double oldLoanCount = Double.valueOf(etLoanCount.getText().toString().trim());//选择的可借入数量
                        String days = loadDeadLine.replace(getString(R.string.day_ri), "");
                        String google = keyBoardInputPopWindow.getEditTextGoogleCode().getText().toString().trim();
                        String rate = DoubleUtils.doubleTransFormatTwo(Double.parseDouble(tvSeekBarRate.getText().toString().replace("%", "")) / 100, 4);//利率
                        if (newLoanCount < oldLoanCount) {//如果可借不足 弹窗提示
                            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.warm_tips));
                            builder.setMessage(getString(R.string.loan_confirm_tips));
                            builder.setNegativeButton(getString(R.string.look_again), (dialog, which) -> dialog.dismiss());
                            builder.setPositiveButton(getString(R.string.continue_), (dialog, which) -> {//按最新价格提交
                                dialog.dismiss();
                                if(newLoanCount<minBorrowAmount){//在判断一次最新的借入数量是否小于最小可借
                                    TipsToast.showTips(getString(R.string.minimum_number_is)+DoubleUtils.doubleTransRound6(minBorrowAmount));
                                    return;
                                }
                                NetApi.createLoan(getActivity(), DoubleUtils.doubleTransRound6(newLoanCount), loanCoin, coinId.get(loanCoin), days, google, rate
                                        , DoubleUtils.doubleTransRound6(Double.parseDouble(mortgageCountInput)), mortgageCoin, coinId.get(mortgageCoin), String.valueOf(price), String.valueOf(timestamp), SPUtil.getToken(), symbol, createLoanObserver);
                            });
                            dialog = builder.create();
                            builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
                            builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {
                            NetApi.createLoan(getActivity(), DoubleUtils.doubleTransRound6(oldLoanCount), loanCoin, coinId.get(loanCoin), days, google, rate
                                    , DoubleUtils.doubleTransRound6(Double.parseDouble(mortgageCountInput)), mortgageCoin, coinId.get(mortgageCoin), String.valueOf(price), String.valueOf(timestamp), SPUtil.getToken(), symbol, createLoanObserver);
                        }
                    }
                } else {
                    TipsToast.showTips(resultBean.message);
                }
            }
        });

    }

    Observer<ResultBean<List<SimpleBean>>> loanTypeObserver = new NetClient.RxObserver<ResultBean<List<SimpleBean>>>() {
        @Override
        public void onSuccess(ResultBean<List<SimpleBean>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    List<SimpleBean> list = resultBean.data;
                    listLoanTypeText.clear();
                    for (int i = 0; i < list.size(); i++) {
                        listLoanTypeText.add(list.get(i).name);
                    }
                    fnLoanType.setText(list.get(0).name);
                }
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }
    };
    Observer<ResultBean<SimpleBean>> getCoinPriceObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    minBorrowAmount=resultBean.data.minBorrowAmount;
                    fnMortgageeCoin.setText(mortgageCoin);//设置默认值
                    fnLoanCoin.setText(loanCoin);//设置默认值
                    setSeekBarMinMaxRate(loanCoin);//设置默认利率
                    fnLoanDeadLinePay.setText("0 " + loanCoin);//设置默认利息
                    price = resultBean.data.price;
                    timestamp = resultBean.data.timestamp;//时间戳
                    symbol = resultBean.data.symbol;//牌价对标识
                    setSeekBarEnabled(true);
                }
            } else {
                ((BaseActivity)getActivity()).setHttpFailed(getActivity(),resultBean);
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            refreshLayout.finishRefresh();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            refreshLayout.finishRefresh();
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
            }
        }
    };

    Observer<ResultBean> createLoanObserver = new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                popupWindow.dismiss();
                //友盟埋点 借款成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.LOAN_SUCCESSFUL);
                TipsToast.showTips(getString(R.string.loan_success));
                CommonUtil.openActicity(getActivity(), MyLoanActivity.class,null);
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }
    };

    Observer<ResultBean<SimpleBean>> getCoinCountObserver=new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null){
                    String mortgageCount=etMortgageCount.getText().toString().trim();
                    double mortgageOver=resultBean.data.amount;
                    //友盟埋点 确认借款
                    UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.LOAN_CONFIRM);
                    if(Double.parseDouble(mortgageCount)>mortgageOver){//可抵押不足
                        //友盟埋点 点击充值
                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.LOAN_GOTORECHARGE);
                        PayNeedReChargePopWindow payNeedReChargePopWindow=new PayNeedReChargePopWindow(getActivity(),mortgageOver,mortgageCoin,Double.parseDouble(mortgageCount));
                        CommonUtil.setBackgroundAlpha(getActivity(), 0.5f);
                        payNeedReChargePopWindow.creatPop().showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);
                    }else{
                        if (popupWindow == null) {//展示谷歌验证码
                            if (keyBoardInputPopWindow == null) {
                                keyBoardInputPopWindow = new KeyBoardInputPopWindow(getActivity(), 2);
                                keyBoardInputPopWindow.setOnOkClick(() -> {
                                    //友盟埋点 确认谷歌验证码
                                    UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.LOAN_SUBMIT_GOOGLECODE);
                                    initCreateLoanData();
                                });
                                keyBoardInputPopWindow.setOnCancelClick(() -> {
                                });
                            }
                            popupWindow = keyBoardInputPopWindow.creatPop();
                        }
                        CommonUtil.setBackgroundAlpha(getActivity(), 0.5f);
                        popupWindow.showAtLocation(btnConfirm, Gravity.BOTTOM, 0, 0);
                        keyBoardInputPopWindow.setConfirmText(getString(R.string.key_board_view_submit_pay));//设置确定文案
                    }
                }
            }else{
                ((BaseActivity)getActivity()).setHttpFailed(getActivity(),resultBean);
            }
        }
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    //选择抵押币种把借入币种相同数据删掉
    private ArrayList<String> calcLoanData() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(listLoanCoinText);
        list.remove(mortgageCoin);
        return list;
    }

    //选择借入币种把抵押币种相同数据删掉
    private ArrayList<String> calcMortgageData() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(listMortgageeCoinText);
        list.remove(loanCoin);
        return list;
    }

    //寻找交易对
    private double getMortgateRate(String loanText, String mortgaStr) {
        for (int i = 0, size = listPairTotal.size(); i < size; i++) {
            LoanPairs loanPairs = listPairTotal.get(i);
            if (loanPairs.borrowCryptoCode.equals(loanText) && loanPairs.mortgageRyptoCode.equals(mortgaStr)) {
                mortgateRate = loanPairs.mortgateRate;
            }
        }
        return mortgateRate;
    }

    private void clearPairs() {
        listPairTotal.clear();
        listMortgageeCoinText.clear();
        listLoanCoinText.clear();
        coinId.clear();
    }

    //设置最小利率
    private void setSeekBarMinMaxRate(String coin) {
        String minRate = "USDT".equals(coin) ? min_usdt_rate * 100 + "%" : min_other_rate * 100 + "%";
        tvSeekBarMin.setText(minRate);//最低利率
        tvSeekBarRate.setText(minRate);//最低利率
        tvSeekBarMax.setText(DoubleUtils.doubleRoundFormat(max_other_rate * 100, 2) + "%");//最高利率
    }

    //获得最小利率的数值
    private double getMinRate() {
        return Double.parseDouble(tvSeekBarMin.getText().toString().replace("%", "")) / 100;
    }

    //获得选择利率的数值
    private double getSelectRate() {
        return Double.parseDouble(tvSeekBarRate.getText().toString().replace("%", "")) / 100;
    }

    //计算展示到期应付利息
    private void calcLoanDeadLineInterest() {
        String loanCount = etLoanCount.getText().toString().trim();
        if (TextUtils.isEmpty(loanCount)) loanCount="0";
        int days = Integer.valueOf(loadDeadLine.replace(getString(R.string.day_ri), ""));
        fnLoanDeadLinePay.setText(DoubleUtils.doubleTransRound6(Double.parseDouble(loanCount) * getSelectRate() * days) + " " + loanCoin);//借入利息
    }

    public void setSeekBarEnabled(boolean enabled) {
        seekBar.setClickable(enabled);
        seekBar.setEnabled(enabled);
        seekBar.setSelected(enabled);
        seekBar.setFocusable(enabled);
    }

    //设置默认状态
    private void setDefaultState() {
        etLoanCount.setFocusable(false);
        etMortgageCount.setFocusable(false);
        etLoanCount.getText().clear();
        etMortgageCount.getText().clear();
        fnLoanDeadLinePay.setText("");
        seekBar.setProgress(0);
        loadDeadLine = listLoadDeadLine.get(0);
        fnLoanDeadLine.setText(loadDeadLine);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

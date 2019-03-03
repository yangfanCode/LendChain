package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lend.lendchain.R;
import com.lend.lendchain.bean.ChangeList;
import com.lend.lendchain.bean.CoinChangeList;
import com.lend.lendchain.bean.CoinChangeRate;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.ChangeListAdapter;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.FrescoUtils;
import com.lend.lendchain.utils.KeyBordUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.lvfq.pickerview.OptionsPickerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class CoinChangeActivity extends BaseActivity {
    @BindView(R.id.coin_change_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.coin_change_tvCoinOut)
    TextView tvCoinOut;
    @BindView(R.id.coin_change_tvCoinIn)
    TextView tvCoinIn;
    @BindView(R.id.coin_change_layoutOut)
    RelativeLayout layoutOut;
    @BindView(R.id.coin_change_layoutIn)
    RelativeLayout layoutIn;
    @BindView(R.id.coin_change_ivIconOut)
    SimpleDraweeView ivCoinOut;
    @BindView(R.id.coin_change_ivIconIn)
    SimpleDraweeView ivIconIn;
    @BindView(R.id.coin_change_tvRate)
    TextView tvRateText;
    @BindView(R.id.coin_change_tvOutLimitText)
    TextView tvOutLimitText;
    @BindView(R.id.coin_change_tvOverAmount)
    TextView tvOverAmount;
    @BindView(R.id.coin_change_etIn)
    EditText etIn;
    @BindView(R.id.coin_change_etOut)
    EditText etOut;
    @BindView(R.id.coin_change_lv)
    ListViewWithOptional lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.coin_change_tvAll)
    TextView tvAll;
    private ArrayList<String> coinPicker;//可兑换币种数据
    private List<CoinChangeList> list;//必中数据
    private boolean isCoinOutSelected = false;//是否选择了转出币种
    private boolean isCoinIntSelected = false;//是否选择了转入币种
    private String coinOutText, coinInText;//picker文案
    private int currentPage=1;
    private ChangeListAdapter adapter;
    private boolean isRefrensh=false;
    private Dialog dialog=null;
    private double instantRate;//转化率

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;//设置布局顶到状态栏
        setContentView(R.layout.activity_coin_change);
        StatusBarUtil.StatusBarLightMode(CoinChangeActivity.this);//状态栏图标白色
        int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0, height, 0, 0);//设置是状态栏渐变色
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTvRightText(getString(R.string.poundage_rule));
        baseTitleBar.setTitle(getString(R.string.change));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setTvRightText(getString(R.string.poundage_rule));
        baseTitleBar.getTvRightText().setTextColor(ColorUtils.COLOR_509FFF);
        baseTitleBar.setTvRightTextClickListener(v -> showDialog());
        list = new ArrayList<>();
        coinPicker = new ArrayList<>();
        KeyBordUtils.setEditTextNoFocus(etOut);
        adapter=new ChangeListAdapter(CoinChangeActivity.this);
        lv.setAdapter(adapter);
        initData();
        initListener();
    }

    private void initData() {
        isRefrensh=true;
        currentPage = 1;
        //获取币种兑换列表
        NetApi.coinChangeList(this, false, SPUtil.getToken(), coinListObserver);
        //获取兑换列表
        NetApi.changeList(this,false,SPUtil.getToken(),currentPage,Constant.PAGE_SIZE,changeListObserver);
    }

    private void reLoadData(){
        isRefrensh=false;
        //获取兑换列表
        NetApi.changeList(this,false,SPUtil.getToken(),++currentPage,Constant.PAGE_SIZE,changeListObserver);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshLayout ->initData());
        refreshLayout.setOnLoadMoreListener(refreshLayout -> reLoadData());
        //提交
        btnConfirm.setOnClickListener(v -> {
            String outCount=etOut.getText().toString().trim();
            String inCount=etIn.getText().toString().trim();
            if(TextUtils.isEmpty(outCount)){
                TipsToast.showTips(getString(R.string.please_input_out_amount));
                return;
            }
            if(TextUtils.isEmpty(inCount)){
                TipsToast.showTips(getString(R.string.please_input_in_amount));
                return;
            }
            NetApi.coinChangeComit(CoinChangeActivity.this,true,SPUtil.getToken(),list.get(coinPicker.indexOf(coinOutText)).id,outCount,list.get(coinPicker.indexOf(coinInText)).id,inCount,changeComitObserver);
        });
        layoutOut.setOnClickListener(v -> {
            OptionsPickerView optionsPickerView = new OptionsPickerView(CoinChangeActivity.this);
            optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                if(coinPicker.get(options1).equals(coinInText)){//选择的两个相同
                    TipsToast.showTips(getString(R.string.can_not_select_same_coin));
                    return;
                }
                FrescoUtils.showThumb(ivCoinOut, list.get(options1).icon);//币种图片
                coinOutText = coinPicker.get(options1);
                tvCoinOut.setText(coinOutText);
                isCoinOutSelected = true;//已经选择了币种
                if (isCoinOutSelected && isCoinIntSelected) {
                    NetApi.coinChangeRate(CoinChangeActivity.this, true, SPUtil.getToken(), list.get(coinPicker.indexOf(coinOutText)).id, list.get(coinPicker.indexOf(coinInText)).id, coinChangeRate);
                } else {
                    tvOutLimitText.setText(Html.fromHtml(getString(R.string.change_out_limit) + "<font color='#509FFF'> -- </font>" + coinOutText));//转出限制默认
                }
                //请求余额
                NetApi.getCoinCount(CoinChangeActivity.this, false, SPUtil.getToken(), Integer.valueOf(list.get(coinPicker.indexOf(coinOutText)).id), getCountObserver);
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(coinPicker);
            optionsPickerView.setSelectOptions(coinPicker.indexOf(coinOutText));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        layoutIn.setOnClickListener(v -> {
            OptionsPickerView optionsPickerView = new OptionsPickerView(CoinChangeActivity.this);
            optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                if(coinPicker.get(options1).equals(coinOutText)){//选择的两个相同
                    TipsToast.showTips(getString(R.string.can_not_select_same_coin));
                    return;
                }
                FrescoUtils.showThumb(ivIconIn, list.get(options1).icon);//币种图片
                coinInText = coinPicker.get(options1);
                tvCoinIn.setText(coinInText);
                isCoinIntSelected = true;//已经选择了币种
                if (isCoinOutSelected && isCoinIntSelected)
                    NetApi.coinChangeRate(CoinChangeActivity.this, true, SPUtil.getToken(), list.get(coinPicker.indexOf(coinOutText)).id, list.get(coinPicker.indexOf(coinInText)).id, coinChangeRate);
            });
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(coinPicker);
            optionsPickerView.setSelectOptions(coinPicker.indexOf(coinInText));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        etOut.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus&&!isCoinOutSelected){
                TipsToast.showTips(getString(R.string.please_select_coinout));
            }
        });
        etIn.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus&&!isCoinOutSelected){
                TipsToast.showTips(getString(R.string.please_select_coinin));
            }
        });
        etOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(coinSelectCheck(s)){
                    if (etOut.isFocused()) {
                        String input = s.toString().trim();
                        if (!TextUtils.isEmpty(input) && !input.endsWith(".")) {
                            //科学计数法
                            etIn.setText(DoubleUtils.doubleTransRound6(DoubleUtils.mul(Double.parseDouble(input),instantRate)));
                        } else {
                            if(TextUtils.isEmpty(input)){
                                etIn.getText().clear();
                            }
                        }
                    }
                }
            }
        });
        etIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(coinSelectCheck(s)){
                    if (etIn.isFocused()) {
                        String input = s.toString().trim();
                        if (!TextUtils.isEmpty(input) && !input.endsWith(".")) {
                            //科学计数法
                            etOut.setText(DoubleUtils.doubleTransRound6(DoubleUtils.div(Double.parseDouble(input),instantRate,6)));
                        } else {
                            if(TextUtils.isEmpty(input)){
                                etOut.getText().clear();
                            }
                        }
                    }
                }
            }
        });
        tvAll.setOnClickListener(v -> CommonUtil.openActicity(CoinChangeActivity.this,ChangeListActivity.class,null));
    }

    //选择币种校验
    private boolean coinSelectCheck(Editable s) {
        if(!isCoinOutSelected){
            TipsToast.showTips(getString(R.string.please_select_coinout));
            s.clear();
            return false;
        }else if(!isCoinIntSelected){
            TipsToast.showTips(getString(R.string.please_select_coinin));
            s.clear();
            return false;
        }
        return true;
    }

    private void showDialog(){
        if(dialog==null){
            CustomDialog.Builder builder=new CustomDialog.Builder(CoinChangeActivity.this);
            View view= LayoutInflater.from(CoinChangeActivity.this).inflate(R.layout.dialog_change,null);
            ImageView ivClose=view.findViewById(R.id.dialog_change_ivClose);
            builder.setContentView(view);
            ivClose.setOnClickListener(v -> dialog.dismiss());
            builder.setScreenRatioX(0.9);
            dialog=builder.create();
            builder.setDialogBackGround(R.drawable.bg_white_radiu5);
        }
        dialog.show();
    }

    Observer<ResultBean<List<CoinChangeList>>> coinListObserver = new NetClient.RxObserver<ResultBean<List<CoinChangeList>>>() {
        @Override
        public void onSuccess(ResultBean<List<CoinChangeList>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                coinPicker.clear();
                list.clear();
                list.addAll(resultBean.data);
                for (int i = 0; i < list.size(); i++) {
                    coinPicker.add(list.get(i).code);
                }
            } else {
                setHttpFailed(CoinChangeActivity.this, resultBean);
            }
        }
    };
    Observer<ResultBean<CoinChangeRate>> coinChangeRate = new NetClient.RxObserver<ResultBean<CoinChangeRate>>() {
        @Override
        public void onSuccess(ResultBean<CoinChangeRate> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                instantRate=resultBean.data.instantRate;
                tvOutLimitText.setText(Html.fromHtml(getString(R.string.change_out_limit) + "<font color='#509FFF'>" + DoubleUtils.doubleTransRound6(resultBean.data.depositMin) + "~" + DoubleUtils.doubleTransRound6(resultBean.data.depositMax) + "</font>" + coinOutText));//转出限制默认
                tvRateText.setText("1 " + coinOutText + "≈" + DoubleUtils.doubleTransRound6(instantRate) + coinInText);
            } else {
                setHttpFailed(CoinChangeActivity.this, resultBean);
            }
        }
    };

    Observer<ResultBean<SimpleBean>> getCountObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    //展示余额
                    tvOverAmount.setText(Html.fromHtml(getString(R.string.over_to_use1)+"<font color='#509FFF'> "+DoubleUtils.doubleTransRound6(resultBean.data.amount)+" </font>"+coinOutText));
                }
            } else {
                setHttpFailed(CoinChangeActivity.this, resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean> changeComitObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null){
                TipsToast.showTips(getString(R.string.change_success));
            }else{
                setHttpFailed(CoinChangeActivity.this, resultBean);
            }
        }
    };

    Observer<ResultBean<ChangeList>> changeListObserver=new NetClient.RxObserver<ResultBean<ChangeList>>() {
        @Override
        public void onSuccess(ResultBean<ChangeList> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null&&resultBean.data.records.size()>0){
                    if(isRefrensh)
                        adapter.loadData(resultBean.data.records);
                    else
                        adapter.reLoadData(resultBean.data.records);
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }else{
                setHttpFailed(CoinChangeActivity.this, resultBean);
            }
        }
        @Override
        public void onCompleted() {
            super.onCompleted();
            finishRefrensh();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            finishRefrensh();
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }
}

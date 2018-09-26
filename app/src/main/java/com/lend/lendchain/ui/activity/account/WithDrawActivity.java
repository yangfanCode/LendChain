package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.DecimalDigitsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class WithDrawActivity extends BaseActivity {

    //    @BindView(R.id.withdraw_tvPaste)
//    TextView tvPaste;
    @BindView(R.id.withdraw_etAdd)
    EditText etAdd;
    @BindView(R.id.withdraw_etCount)
    DecimalDigitsEditText etCount;
    @BindView(R.id.withdraw_etMemo)
    EditText etMemo;
    @BindView(R.id.withdraw_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.withdraw_tvPoundage)
    TextView tvPoundage;
    @BindView(R.id.withdraw_tvRealMoney)
    TextView tvRealMoney;
    @BindView(R.id.withdraw_tvRealMoneyCoin)
    TextView tvRealMoneyCoin;
    @BindView(R.id.withdraw_tvPoundageCoin)
    TextView tvPoundageCoin;
    @BindView(R.id.withdraw_tvLimit)
    TextView tvLimit;
    @BindView(R.id.withdraw_tvCoin)
    TextView tvCoin;
    @BindView(R.id.with_draw_llMemo)
    LinearLayout llMemo;
    private String cryptoId, cryptoCode, id;
    private double withdrawFee, minWithdraw;//手续费
    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        StatusBarUtil.setStatusBarColor(WithDrawActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(WithDrawActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.withdraw));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        cryptoId = getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
        cryptoCode = getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        id = getIntent().getExtras().getString(Constant.ARGS_PARAM2);
        etCount.setAfterDot(("LV".equals(cryptoCode) || "GXS".equals(cryptoCode)) ?5:6);//lv gxs 5位小数
        llMemo.setVisibility(("LV".equals(cryptoCode) || "GXS".equals(cryptoCode)) ? View.VISIBLE : View.GONE);
        tvRealMoneyCoin.setText(cryptoCode);
        tvPoundageCoin.setText(cryptoCode);
        tvCoin.setText(cryptoCode);
        initData();
        initListener();
    }

    private void initData() {
        NetApi.coinAttribute(WithDrawActivity.this, true, SPUtil.getToken(), cryptoId, withDrawObserver);
    }

    private void initListener() {
//        tvPaste.setOnClickListener(v -> {
//            String str = CommonUtil.clipboardPasteStr();
//            if (!TextUtils.isEmpty(str)) {
//                etAdd.setText(str);
//            }
//        });
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input) && !input.endsWith(".") && Double.valueOf(input) >= minWithdraw) {
                    tvRealMoney.setText(DoubleUtils.doubleTransRound6(Double.parseDouble(input) - withdrawFee));
                } else {
                    tvRealMoney.setText("0");
                }
            }
        });
        btnConfirm.setOnClickListener(v -> {
            String address = etAdd.getText().toString().trim();
            String count = etCount.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                TipsToast.showTips(getString(R.string.please_input_withdraw_add));
                return;
            }
            if (TextUtils.isEmpty(count)) {
                TipsToast.showTips(getString(R.string.please_input_withdraw_count));
                return;
            }
            if (!TextUtils.isEmpty(count) && Double.valueOf(count) < minWithdraw) {
                TipsToast.showTips(getString(R.string.withdraw_min_count) + DoubleUtils.doubleTransRound6(minWithdraw));
                return;
            }
            //读取用户此币种余额 是否不足
            NetApi.getCoinCount(WithDrawActivity.this,false, SPUtil.getToken(), Integer.valueOf(cryptoId), getCoinCountObserver);
        });
    }

    Observer<ResultBean<SimpleBean>> withDrawObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    withdrawFee = resultBean.data.withdrawFee;
                    minWithdraw = resultBean.data.minWithdraw;//最小提现
                    tvPoundage.setText(DoubleUtils.doubleTransRound6(withdrawFee));//手续费
                    etCount.setHint(getString(R.string.withdraw_min_count) + minWithdraw);
                    tvLimit.setText(String.format(tvLimit.getText().toString(), cryptoCode, String.valueOf(DoubleUtils.doubleTransRoundTwo(resultBean.data.dayWithdraw, 2))));//每日限额
                }
            } else {
                setHttpFailed(WithDrawActivity.this, resultBean);
            }
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
                    String count=etCount.getText().toString().trim();
                    double over=resultBean.data.amount;
                    if(Double.parseDouble(count)>over){
                        TipsToast.showTips(getString(R.string.over_lack));
                    }else{
                        String address = etAdd.getText().toString().trim();
                        String memo = etMemo.getText().toString().trim();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ARGS_PARAM1, address);
                        bundle.putString(Constant.ARGS_PARAM2, count);
                        bundle.putString(Constant.ARGS_PARAM3, memo);
                        bundle.putString(Constant.ARGS_PARAM4, id);
                        CommonUtil.openActicity(WithDrawActivity.this, WithDrawCertifyActivity.class, bundle);
                    }
                }
            }else{
                setHttpFailed(WithDrawActivity.this,resultBean);
            }
        }
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

}

package com.lend.lendchain.ui.activity.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.QRCodeUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class RechargeActivity extends BaseActivity {
    @BindView(R.id.recharge_tvAddress)
    TextView tvAddress;
    @BindView(R.id.recharge_ivQRCode)
    ImageView ivQRCode;
    @BindView(R.id.recharge_tvCopy)
    TextView tvCopy;
    @BindView(R.id.recharge_tvCopyM)
    TextView tvCopyM;
    @BindView(R.id.recharge_tvMemo)
    TextView tvMemo;
    @BindView(R.id.recharge_tvTips)
    TextView tvTips;
    @BindView(R.id.recharge_llMemo)
    LinearLayout llMemo;
    private String add , code, memo, cryptoId;//币种

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        StatusBarUtil.setStatusBarColor(RechargeActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(RechargeActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            add = getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
            memo = getIntent().getExtras().getString(Constant.ARGS_PARAM1);
            code = getIntent().getExtras().getString(Constant.ARGS_PARAM2);
            cryptoId = getIntent().getExtras().getString(Constant.ARGS_PARAM3);
        }
        baseTitleBar.setTitle(getString(R.string.recharge));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        initData();
        initListener();
    }

    private void initData() {
        if(TextUtils.isEmpty(add))add="(null)";
        NetApi.coinAttribute(RechargeActivity.this, true, SPUtil.getToken(), cryptoId, rechargeObserver);
        tvAddress.setText(add);
        llMemo.setVisibility(("GXS".equals(code) || "LV".equals(code)) ? View.VISIBLE : View.GONE);
        if (llMemo.getVisibility() == View.VISIBLE) tvMemo.setText(memo);

        //展示二维码
        ivQRCode.setImageBitmap(QRCodeUtil.createQrcode(add, DisplayUtil.dp2px(RechargeActivity.this, 95), 1));
    }


    private void initListener() {
        tvCopy.setOnClickListener(v -> {
            CommonUtil.clipboardStr(add);
            TipsToast.showTips(getString(R.string.copy_success));
        });
        tvCopyM.setOnClickListener(v -> {
            CommonUtil.clipboardStr(memo);
            TipsToast.showTips(getString(R.string.copy_success));
        });
    }

    Observer<ResultBean<SimpleBean>> rechargeObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @SuppressLint("StringFormatMatches")
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    tvTips.setText(String.format(getString(R.string.recharge_tips), code,resultBean.data.accountNum,resultBean.data.withdrawNum, DoubleUtils.doubleTransRound6(resultBean.data.minDeposit), code));
                }
            } else {
                setHttpFailed(RechargeActivity.this, resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
}

package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.GetBlockCityRecharge;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.MyWalletActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 充值提现状态
 */
public class RechargeWithDrawStateActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recharge_withdraw_state_ivState)
    ImageView ivState;
    @BindView(R.id.recharge_withdraw_state_tvState)
    TextView tvState;
    @BindView(R.id.recharge_withdraw_state_tvCoinText)
    TextView tvCoinText;
    @BindView(R.id.recharge_withdraw_state_tvCoinCount)
    TextView tvCoinCount;
    @BindView(R.id.recharge_withdraw_state_tvSubmit1)
    TextView tvSubmit1;
    @BindView(R.id.recharge_withdraw_state_tvSubmit2)
    TextView tvSubmit2;
    private String status, code, count, orderId, tradeNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_with_draw_state);
        StatusBarUtil.setStatusBarColor(RechargeWithDrawStateActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(RechargeWithDrawStateActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        Uri uri = getIntent().getData();
        if (uri != null) {
            orderId=uri.getQueryParameter("orderId");
            if(orderId.contains("split")){
                orderId=orderId.split("split")[0];
            }
        }
        initData();
        initListener();
    }

    private void initListener() {
        tvSubmit1.setOnClickListener(v -> CommonUtil.openActicity(this,RechangeWithdrawRecordActivity.class,null));
    }

    private void initData() {
        NetApi.getBlockCityRecharge(this, true, SPUtil.getToken(), orderId, getBlockObserver);
    }

    //充值成功
    private void rechargeSuccess() {
        ivState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rechargewith_success));
        tvState.setText(getString(R.string.recharge_success));
        tvCoinText.setText(getString(R.string.recharge_success_text) + " (" + code + ")");
        tvCoinCount.setText(count);
        baseTitleBar.setTitle(getString(R.string.recharge_success));
        tvSubmit2.setText(getString(R.string.recharge_continue));
        tvSubmit2.setOnClickListener(v -> CommonUtil.openActicity(RechargeWithDrawStateActivity.this,MyWalletActivity.class,null));
    }

    //充值失败
    private void rechargeFailed() {
        ivState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rechargewith_failed));
        tvState.setText(getString(R.string.recharge_failed));
        tvCoinCount.setVisibility(View.GONE);
        tvCoinText.setText(Html.fromHtml(getString(R.string.recharge_amount) + ":" + "<font color='#509FFF'>" + count + " " + code + "</font>"));
        baseTitleBar.setTitle(getString(R.string.recharge_failed));
        tvSubmit2.setText(getString(R.string.recharge_retry));
        tvSubmit2.setOnClickListener(v -> {
            //跳转布洛克城支付
            try {
                Uri uri = Uri.parse("blockcity://pay?tradeNo=" + tradeNo + "&callbackUrl=" + URLEncoder.encode("lendchain://pay/result?orderId=" + orderId+"split", "UTF-8"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    //充值待支付
    private void rechargeWaitPay() {
        ivState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rechargewith_waiting));
        tvState.setText(getString(R.string.wait_pay));
        tvCoinCount.setVisibility(View.GONE);
        tvCoinText.setText(Html.fromHtml(getString(R.string.recharge_amount) + ":" + "<font color='#509FFF'>" + count + " " + code + "</font>"));
        baseTitleBar.setTitle(getString(R.string.wait_pay));
        tvSubmit2.setText(getString(R.string.pay_now));
        tvSubmit2.setOnClickListener(v -> {

        });
    }

    //充值处理中
    private void rechargeing() {
        ivState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rechargewith_waiting));
        tvState.setText(getString(R.string.processing));
        tvCoinCount.setVisibility(View.GONE);
        tvCoinText.setText(Html.fromHtml(getString(R.string.recharge_amount) + ":" + "<font color='#509FFF'>" + count + " " + code + "</font>"));
        baseTitleBar.setTitle(getString(R.string.processing));
        tvSubmit2.setText(getString(R.string.order_refrensh));
        tvSubmit2.setOnClickListener(v -> {//刷新订单
            if (!CommonUtils.isFastDoubleClick(1)) {
                NetApi.getBlockCityRecharge(RechargeWithDrawStateActivity.this, true, SPUtil.getToken(), orderId, getBlockObserver);
            }
        });
    }

    //充值订单失效
    private void rechargeCancle() {
        ivState.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_rechargewith_cancle));
        tvState.setText(getString(R.string.order_cancle));
        tvCoinCount.setVisibility(View.GONE);
        tvCoinText.setText(Html.fromHtml(getString(R.string.recharge_amount) + ":" + "<font color='#509FFF'>" + count + " " + code + "</font>"));
        baseTitleBar.setTitle(getString(R.string.order_cancle));
        tvSubmit2.setText(getString(R.string.recharge_retry));
        tvSubmit2.setOnClickListener(v -> {//重新充值
            //跳转布洛克城支付
            try {
                Uri uri = Uri.parse("blockcity://pay?tradeNo=" + tradeNo + "&callbackUrl=" + URLEncoder.encode("lendchain://pay/result?orderId=" + orderId+"split", "UTF-8"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    //查询布洛克城充值信息
    Observer<ResultBean<GetBlockCityRecharge>> getBlockObserver = new NetClient.RxObserver<ResultBean<GetBlockCityRecharge>>() {
        @Override
        public void onSuccess(ResultBean<GetBlockCityRecharge> getBlockCityRechargeResultBean) {
            if (getBlockCityRechargeResultBean == null) return;
            if (getBlockCityRechargeResultBean.isSuccess()) {
                if (getBlockCityRechargeResultBean == null) return;
                if (getBlockCityRechargeResultBean.isSuccess()) {
                    code=getBlockCityRechargeResultBean.data.cryptoCode;
                    count= DoubleUtils.doubleTransRound6(getBlockCityRechargeResultBean.data.amount);
                    status = getBlockCityRechargeResultBean.data.status;
                    tradeNo = getBlockCityRechargeResultBean.data.tradeNo;
                    if ("0".equals(status)) {
                        rechargeWaitPay();//待支付
                    } else if ("1".equals(status)) {
                        rechargeing();//处理中
                    } else if ("2".equals(status)) {
                        rechargeFailed();//充值失败
                    } else if ("3".equals(status)) {
                        rechargeSuccess();//充值成功
                    } else {
                        rechargeCancle();//订单失效
                    }
                }
            } else {
                setHttpFailed(RechargeWithDrawStateActivity.this, getBlockCityRechargeResultBean);
            }
        }
    };
}

package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        initData();
        initListener();
    }

    private void initListener() {
    }

    private void initData() {
    }

}

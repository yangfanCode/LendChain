package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.APPCommonNavigatorAdapter;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.MainActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.LVSendFragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.ReChargeRecordFragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.TransferRecordFragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.WithDrawRecordFragment;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.StatusBarUtil;
import com.yangfan.widget.CustomFragmentPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 充提记录
 */
public class RechangeWithdrawRecordActivity extends BaseActivity {

    @BindView(R.id.recharge_withdraw_viewPager)
    ViewPager viewPager;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    private ReChargeRecordFragment reChargeRecordFragment;
    private boolean isGotoMain;//是否跳转个人中心

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;
        setContentView(R.layout.activity_rechange_withdraw_record);
        int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0, height, 0, 0);
        StatusBarUtil.StatusBarLightMode(RechangeWithdrawRecordActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isGotoMain=intent.getBooleanExtra(Constant.INTENT_EXTRA_DATA,false);
        }
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.my_amount_record));
        baseTitleBar.setLayLeftBackClickListener(v -> onBackPressed());
        isGotoMain=getIntent().getBooleanExtra(Constant.INTENT_EXTRA_DATA,false);
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        reChargeRecordFragment=ReChargeRecordFragment.newInstance();
        adapter.addFrag(reChargeRecordFragment, getString(R.string.recharge));
        adapter.addFrag(WithDrawRecordFragment.newInstance(), getString(R.string.withdraw));
        adapter.addFrag(TransferRecordFragment.newInstance(), getString(R.string.transfer));
        adapter.addFrag(LVSendFragment.newInstance(), getString(R.string.send_LV));
        viewPager.setAdapter(adapter);
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdjustMode(true);
        navigator.setAdapter(new APPCommonNavigatorAdapter(adapter.getTitles(), viewPager));
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    public void onBackPressed() {
        if(isGotoMain){
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ARGS_PARAM1, Constant.LOGIN);
            CommonUtil.openActicity(RechangeWithdrawRecordActivity.this, MainActivity.class, bundle);
        }else{
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //充值返回刷新
        if(viewPager.getCurrentItem()==0){
            reChargeRecordFragment.initData(true);
        }
    }
}

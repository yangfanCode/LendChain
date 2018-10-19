package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.APPCommonNavigatorAdapter;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.BlockCityRechargeragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.NomalRechargeFragment;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.CustomFragmentPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RechargeActivity extends BaseActivity {
    @BindView(R.id.recharge_viewPager)
    ViewPager viewPager;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
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
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        String lan= LanguageUtils.getUserLanguageSetting();
        if(LanguageUtils.SIMPLIFIED_CHINESE.equals(lan)){//只有中文显示 布洛克
            adapter.addFrag(NomalRechargeFragment.newInstance(add,memo,code,cryptoId), getString(R.string.number_wallet_recharge));
            adapter.addFrag(BlockCityRechargeragment.newInstance(code), getString(R.string.blockcity_wallet_recharge));
        }else{
            adapter.addFrag(NomalRechargeFragment.newInstance(add,memo,code,cryptoId), getString(R.string.number_wallet_recharge));
            magicIndicator.setVisibility(View.GONE);//隐藏tab
        }
        viewPager.setAdapter(adapter);
        CommonNavigator navigator = new CommonNavigator(this);
        navigator.setAdjustMode(true);
        navigator.setAdapter(new APPCommonNavigatorAdapter(adapter.getTitles(), viewPager));
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }
}

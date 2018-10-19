package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.APPCommonNavigatorAdapter;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.BlockCityWithDrawFragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.NomalWithDrawFragment;
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

public class WithDrawActivity extends BaseActivity {

    @BindView(R.id.withdraw_viewPager)
    ViewPager viewPager;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    private String cryptoId, cryptoCode, id,count;
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
        count = getIntent().getExtras().getString(Constant.ARGS_PARAM3);
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        String lan= LanguageUtils.getUserLanguageSetting();
        if(LanguageUtils.SIMPLIFIED_CHINESE.equals(lan)) {//只有中文显示 布洛克
            adapter.addFrag(NomalWithDrawFragment.newInstance(cryptoId,cryptoCode,id,count), getString(R.string.number_wallet_withdraw));
            adapter.addFrag(BlockCityWithDrawFragment.newInstance(cryptoId,cryptoCode,id,count), getString(R.string.blockcity_wallet_withdraw));
        }else{
            adapter.addFrag(NomalWithDrawFragment.newInstance(cryptoId,cryptoCode,id,count), getString(R.string.number_wallet_withdraw));
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

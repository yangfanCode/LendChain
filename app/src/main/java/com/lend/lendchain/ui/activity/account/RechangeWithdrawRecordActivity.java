package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.ReChargeRecordFragment;
import com.lend.lendchain.ui.fragment.rechargewithdraw.WithDrawRecordFragment;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.yangfan.widget.CustomFragmentPagerAdapter;
import com.yangfan.widget.CustomTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 充提记录
 */
public class RechangeWithdrawRecordActivity extends BaseActivity {

    @BindView(R.id.recharge_withdraw_viewPager)
    ViewPager viewPager;
    @BindView(R.id.customTabLayout)
    CustomTabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows=false;
        setContentView(R.layout.activity_rechange_withdraw_record);
        int height= CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0,height,0,0);
        StatusBarUtil.StatusBarLightMode(RechangeWithdrawRecordActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.recharge_withdraw_record));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        tabLayout.setViewHeight(DisplayUtil.dp2px(this,40));
        tabLayout.setBottomLineWidth(DisplayUtil.dp2px(this,87));
        tabLayout.setBottomLineHeight(DisplayUtil.dp2px(this,2));
        tabLayout.setBottomLineHeightBgResId(R.color.color_509FFF);
        tabLayout.setmTextColorSelect(ColorUtils.COLOR_509FFF);
        tabLayout.setmTextColorUnSelect(ColorUtils.COLOR_999999);
        tabLayout.setTextSize(14);
        int width=getResources().getDisplayMetrics().widthPixels;
        tabLayout.setNeedEqual(true,width);
        CustomFragmentPagerAdapter adapter=new CustomFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ReChargeRecordFragment.newInstance(),getString(R.string.recharge_record));
        adapter.addFrag(WithDrawRecordFragment.newInstance(),getString(R.string.withdraw_record));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }
}

package com.lend.lendchain.ui.fragment;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.APPCommonNavigatorAdapter;
import com.lend.lendchain.ui.fragment.invest.InvestAllFragment;
import com.lend.lendchain.ui.fragment.invest.InvestMortGagaFragment;
import com.lend.lendchain.ui.fragment.invest.InvestPlatFormFragment;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.widget.BaseTitleBar;
import com.yangfan.widget.CustomFragmentPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投资fragment
 */
public class InvestFragment extends BaseFragment {
    private String tag = "InvestFragment";
    @BindView(R.id.base_title_bar)
    BaseTitleBar baseTitleBar;
    //    @BindView(R.id.invest_tabLayout)
//    CustomTabLayout tabLayout;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.invest_viewPager)
    ViewPager viewPager;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_invest, container,
                    false);
            int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
            parentView.setPadding(0, height, 0, 0);
            initView();
            initData();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);

        return parentView;
    }

    private void initData() {
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFrag(InvestAllFragment.newInstance(), getString(R.string.all));
        adapter.addFrag(InvestPlatFormFragment.newInstance(), getString(R.string.platForm));
        adapter.addFrag(InvestMortGagaFragment.newInstance(), getString(R.string.mortgage));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        CommonNavigator navigator=new CommonNavigator(getActivity());
        navigator.setAdapter(new APPCommonNavigatorAdapter(adapter.getTitles(),viewPager));
        navigator.setAdjustMode(true);
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initView() {
        ButterKnife.bind(this, parentView);
        baseTitleBar.getImvLeftBack().setVisibility(View.GONE);
        baseTitleBar.setTitle(getString(R.string.invest));
    }
    //每次都刷新数据
    @Override
    public void onResume() {
        super.onResume();
        //友盟页面统计混乱修复
//        if(getUserVisibleHint()){
//            onVisibilityChangedToUser(true, tag);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //友盟页面统计混乱修复
//        onVisibilityChangedToUser(false, tag);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isResumed()){
//            onVisibilityChangedToUser(isVisibleToUser, tag);
//        }
//    }

    @Override
    protected void onVisible() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

package com.lend.lendchain.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.fragment.invest.InvestAllFragment;
import com.lend.lendchain.ui.fragment.invest.InvestMortGagaFragment;
import com.lend.lendchain.ui.fragment.invest.InvestPlatFormFragment;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.widget.BaseTitleBar;
import com.yangfan.widget.CustomFragmentPagerAdapter;
import com.yangfan.widget.CustomTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投资fragment
 */
public class InvestFragment extends Fragment {
    @BindView(R.id.base_title_bar)
    BaseTitleBar baseTitleBar;
    @BindView(R.id.invest_tabLayout)
    CustomTabLayout tabLayout;
    @BindView(R.id.invest_viewPager)
    ViewPager viewPager;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_invest, container,
                    false);
            int height= CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
            parentView.setPadding(0,height,0,0);
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
        CustomFragmentPagerAdapter adapter=new CustomFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFrag(InvestAllFragment.newInstance(),getString(R.string.all));
        adapter.addFrag(InvestPlatFormFragment.newInstance(),getString(R.string.platForm));
        adapter.addFrag(InvestMortGagaFragment.newInstance(),getString(R.string.mortgage));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        ButterKnife.bind(this, parentView);
        baseTitleBar.getImvLeftBack().setVisibility(View.GONE);
        baseTitleBar.setTitle(getString(R.string.invest));
        tabLayout.setViewHeight(DisplayUtil.dp2px(getActivity(),40));
        tabLayout.setBottomLineWidth(DisplayUtil.dp2px(getActivity(),60));
        tabLayout.setBottomLineHeight(DisplayUtil.dp2px(getActivity(),2));
        tabLayout.setBottomLineHeightBgResId(R.color.color_509FFF);
        tabLayout.setmTextColorSelect(ColorUtils.COLOR_509FFF);
        tabLayout.setmTextColorUnSelect(ColorUtils.COLOR_999999);
        tabLayout.setTextSize(14);
        int width=getResources().getDisplayMetrics().widthPixels;
        tabLayout.setNeedEqual(true,width);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

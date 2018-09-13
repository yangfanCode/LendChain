package com.lend.lendchain.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;

import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.DisplayUtil;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * CommonNavigatorAdapter的通用类
 */
public class APPCommonNavigatorAdapter extends CommonNavigatorAdapter {
    //默认属性
    private final int DEFAULT_NOMAL_COLOR=ColorUtils.COLOR_999999;//默认正常颜色
    private final int DEFAULT_SELECT_COLOR=ColorUtils.COLOR_509FFF;//默认选中颜色
    private final int DEFAULT_TEXT_SIZE=14;//默认字号14sp textview默认14sp
    private List<CharSequence> mTitleDataList;
    private ViewPager viewPager;
    private ColorTransitionPagerTitleView colorTransitionPagerTitleView;

    public APPCommonNavigatorAdapter(List<CharSequence> mTitleDataList, ViewPager viewPager) {
        this.mTitleDataList = mTitleDataList;
        this.viewPager = viewPager;
    }

    public APPCommonNavigatorAdapter(List<CharSequence> mTitleDataList, ViewPager viewPager, ColorTransitionPagerTitleView colorTransitionPagerTitleView) {
        this.mTitleDataList = mTitleDataList;
        this.viewPager = viewPager;
        this.colorTransitionPagerTitleView = colorTransitionPagerTitleView;
    }

    @Override
    public int getCount() {
        return mTitleDataList != null && mTitleDataList.size() > 0 ? mTitleDataList.size() : 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);//这里每次需要创建新的titleView
        if (this.colorTransitionPagerTitleView == null) {//没传入自定义属性 使用默认样式
            //默认属性
            colorTransitionPagerTitleView.setNormalColor(ColorUtils.COLOR_999999);
            colorTransitionPagerTitleView.setSelectedColor(ColorUtils.COLOR_509FFF);
            colorTransitionPagerTitleView.setTextSize(14);
            colorTransitionPagerTitleView.setText(mTitleDataList.get(index));
            colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
            return colorTransitionPagerTitleView;
        }else{
            colorTransitionPagerTitleView.setNormalColor(this.colorTransitionPagerTitleView.getNormalColor()==0?DEFAULT_NOMAL_COLOR:this.colorTransitionPagerTitleView.getNormalColor());
            colorTransitionPagerTitleView.setSelectedColor(this.colorTransitionPagerTitleView.getSelectedColor()==0?DEFAULT_SELECT_COLOR:this.colorTransitionPagerTitleView.getSelectedColor());
            //getTextSize 单位px 转sp
            colorTransitionPagerTitleView.setTextSize((int)this.colorTransitionPagerTitleView.getTextSize()==DisplayUtil.sp2px(context,DEFAULT_TEXT_SIZE)?DEFAULT_TEXT_SIZE:DisplayUtil.px2sp(context,this.colorTransitionPagerTitleView.getTextSize()));
            colorTransitionPagerTitleView.setText(mTitleDataList.get(index));
            colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
            return colorTransitionPagerTitleView;
        }
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setColors(ColorUtils.COLOR_509FFF);
        indicator.setRoundRadius(DisplayUtil.dp2px(context, 1f));
        indicator.setLineHeight(DisplayUtil.dp2px(context, 2f));
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        return indicator;
    }
}

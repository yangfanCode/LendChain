package com.lend.lendchain.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.GuideViewPageAdapter;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuidePageActivity extends Activity implements OnPageChangeListener, OnClickListener {
    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    private List<View> views;
    private static final int[] pics = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidepage);
        SPUtil.put(SPUtil.KEY_VERSIONID, CommonUtil.getVersionId(GuidePageActivity.this));
        initView();
    }

    public void initView() {
        ButterKnife.bind(this);
        views = new ArrayList<>();
        // 初始化引导图片列表
        for (int resId : pics) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ScaleType.CENTER_CROP);
            iv.setImageResource(resId);
            iv.setOnClickListener(this);
            views.add(iv);
        }
        // 初始化Adapter
        GuideViewPageAdapter vpAdapter = new GuideViewPageAdapter(views);
        vpGuide.setAdapter(vpAdapter);
        // 绑定回调
        vpGuide.addOnPageChangeListener(this);
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        //
    }

    // 当前页面被滑动时调用 0909 0516 2249
    @Override
    public void onPageScrolled(int nowpage, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        currentIndex = arg0;
    }

    @Override
    public void onClick(View v) {
        if (currentIndex == 3) {
            CommonUtil.openActicity(GuidePageActivity.this, MainActivity.class,
                    null, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

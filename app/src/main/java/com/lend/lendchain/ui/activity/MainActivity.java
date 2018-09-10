package com.lend.lendchain.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.ui.fragment.HomeFragment;
import com.lend.lendchain.ui.fragment.InvestFragment;
import com.lend.lendchain.ui.fragment.LoanFragment;
import com.lend.lendchain.ui.fragment.MineFragment;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.LayoutTabItem;
import com.lend.lendchain.widget.TipsToast;


public class MainActivity extends BaseActivity {
    public static MainActivity instance = null;
    private boolean mExitFlag;// 退出标记
    private FragmentTabHost mTabHost;
    private final Class[] fragments = {HomeFragment.class, InvestFragment.class, LoanFragment.class, MineFragment.class};
    private int mTextviewArray[] = {R.string.tab_home, R.string.tab_invest, R.string.tab_loan, R.string.tab_mine};
    private int mImageViewArray[] = {R.drawable.icon_home_selector, R.drawable.icon_invest_selector, R.drawable.icon_loan_selector,
            R.drawable.icon_mine_selector};
    private int currentTab;
    private boolean mIsAfterSaveInstance = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;//首页设置沉浸式 home沉浸式 其他fragment设置状态栏颜色
        setContentView(R.layout.activity_main);
        instance=this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            if(Constant.LOGIN.equals(intent.getExtras().getString(Constant.ARGS_PARAM1))){//登录操作
                currentTab = 3;//回到个人中心
                mIsAfterSaveInstance = false;//重新intent后不走onResumeFragments
                setCurrentTab(currentTab);
            }
        }
    }

    @Override
    public void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        // 得到fragment的个数
        int count = fragments.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(mTextviewArray[i]))
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragments[i], null);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId != null) {
                    if (tabId.equals(getString(mTextviewArray[0]))) {
                        StatusBarUtil.StatusBarDarkMode(MainActivity.this);
//                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.HOME_PAGE);
                        currentTab = 0;
//                        StatusBarUtil.StatusBarDarkMode(mActivity, type);
                    } else if (tabId.equals(getString(mTextviewArray[1]))) {

                        StatusBarUtil.StatusBarLightMode(MainActivity.this);
//                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.NEWS_PAGE);
                        currentTab = 1;
//                        type = StatusBarUtil.StatusBarLightMode(mActivity);
                    } else if (tabId.equals(getString(mTextviewArray[2]))) {
                        StatusBarUtil.StatusBarLightMode(MainActivity.this);
//                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.EXCHANGE_PAGE);
                        currentTab = 2;
//                        type = StatusBarUtil.StatusBarLightMode(mActivity);
                    } else if (tabId.equals(getString(mTextviewArray[3]))) {
                        StatusBarUtil.StatusBarDarkMode(MainActivity.this);
//                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_PAGE);
                        currentTab = 3;
//                        type = StatusBarUtil.StatusBarDarkMode(mActivity);
                    }
                }
            }
        });
        currentTab = 0;
//        Intent intent = getIntent();
//        if (intent != null && intent.getExtras() != null) {
//            Bundle bundle = intent.getExtras();
//            currentTab = bundle.getInt(Constant.INTENT_EXTRA_DATA, 0);
//        }
        setCurrentTab(currentTab);

    }


    private View getTabItemView(int index) {
        LayoutTabItem layoutTabItem = new LayoutTabItem(this);
        layoutTabItem.setTab(mImageViewArray[index], getString(mTextviewArray[index]));
        return layoutTabItem;
    }

    /**
     * 只有在onSaveInstanceState之前做Fragment切换，否则会报
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     */
    public void setCurrentTab(int index) {
        if (null != mTabHost && !mIsAfterSaveInstance)
            mTabHost.setCurrentTab(index);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mIsAfterSaveInstance = false;
        LogUtils.LogE(MainActivity.class, "onResumeFragments()");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsAfterSaveInstance = true;
        outState.putString("tab", mTabHost.getCurrentTabTag());
        LogUtils.LogE(MainActivity.class, "onSaveInstanceState()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        String frgTag = mTabHost.getCurrentTabTag();
//        Fragment fragment=  getSupportFragmentManager().findFragmentByTag(frgTag);
//        if(fragment!=null){
//            if(fragment instanceof MineFragment&&currentTab==3){
//                ((MineFragment)fragment).setRefrensh();
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        if (mExitFlag) {
            ContextHelper.appExit(this);
        } else {
            TipsToast.showTips(getString(R.string.app_exit));
            mExitFlag = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExitFlag = false;
                }
            }, 3000);
        }
    }



}

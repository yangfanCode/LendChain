package com.lend.lendchain.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lend.lendchain.MyApplication;
import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.AndroidMHelper;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.service.Service;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.BaseTitleBar;
import com.lend.lendchain.widget.TipsToast;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yangfan.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;


/**
 * Created by yangfan
 * nrainyseason@163.com
 */


public abstract class BaseActivity extends FragmentActivity {

    public abstract void initView();

    protected BaseTitleBar baseTitleBar;
    protected ImageView imvLeftBack, imvShare, imvCollect;
    protected TextView tvTitle, tvRightTitle;

    public Subscription subscription;
    //public Login loginInfo;
    public String userId = "";
    public Gson gson;

    public Activity mActivity = null;
    public boolean dispatchTouchEvent = true;

    public boolean onPauseIsLogin;// 记录页面暂停时的登录状态
    public boolean isFirstOnResume = true;
    protected boolean isOnResume = true;
    protected boolean isCanRequest = true;// true 可以请求

    protected boolean notSetStatusBarTintResource = false;// 是否 不改变状态栏
    protected boolean fitSystemWindows = true;//true 不上到状态来  false顶到状态栏
//    protected int statusBarTintResource = R.color.color_statusbar_bg;
    protected String languageSetting = "";
    private List<Subscription> mSubscription = new ArrayList<>();//订阅管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSystemBarColor(R.color.transparent,true);//全局状态栏颜色
        StatusBarUtil.transparencyBar(this);
        //MobSDK.init(this, "23cd30b2926a0", "3e5fa99b6d6709e6eee49b3622612fde");//sharedSDK
        PushAgent.getInstance(this).onAppStart();//友盟统计添加
        ContextHelper.addActivity(this);
        ContextHelper.setLastActivity(this);
        if (!notSetStatusBarTintResource) {
            /**
             *该部分注释代码 主要是 用来设置透明状态栏，以及设置状态栏颜色
             *作用 同 style ：AppTheme 中的  colorPrimaryDark（android 4.4 设置无效，下面注释代码可以）
             */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                setTranslucentStatus(true);
//            }
//            setStatusBarTintResource(statusBarTintResource);
//            StatusBarUtil.transparencyBar(this);
//            StatusBarUtil.setStatusBarColor(this, statusBarTintResource);
//            StatusBarUtil.StatusBarLightMode(this);
        }

        mActivity = this;
        gson = new Gson();
        initLangeuage();//修复安卓7.0以后 webview多语言失效
//        languageSetting = CommonUtil2.getLanguageSetting();
//        CommonUtil2.saveLanguageSetting(languageSetting);
//        EventBus.getDefault().register(this);
    }

    //修改状态栏颜色和图标颜色
    public void setSystemBarColor(int colorId,boolean isDark){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){//5.0
            Window window = getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(colorId);
            //修改系统图标颜色
            new AndroidMHelper().setStatusBarLightMode(this, isDark);
        }
    }

    //统一设置透明状态栏 和 fitsystemwindow=true
    private void setFitsSystemWindows(boolean fitSystemWindows) {
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        if (contentFrameLayout != null) {
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(fitSystemWindows);
                // 作用 同 在 布局文件 根布局 里  添加 android:fitsSystemWindows="true"
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (fitSystemWindows)
            setFitsSystemWindows(fitSystemWindows);
        baseTitleBar = (BaseTitleBar) findViewById(R.id.base_title_bar);
        //getLoginData();
        if (tvTitle != null)
            tvTitle.getPaint().setFakeBoldText(true);

        if (baseTitleBar != null) {
            imvLeftBack = baseTitleBar.getImvLeftBack();
            tvTitle = baseTitleBar.getTvTitle();
            imvShare = baseTitleBar.getImvShare();
            imvCollect = baseTitleBar.getImvCollect();
            tvRightTitle = baseTitleBar.getTvRightText();
            baseTitleBar.setTitle(getTitle());
        }
        initView();

    }


    public void setOnClick(OnClickListener lis) {

        if (baseTitleBar != null) {
            baseTitleBar.setLayLeftBackClickListener(lis);
            baseTitleBar.setImvCollectClickListener(lis);
            baseTitleBar.setImvShareClickListener(lis);
            baseTitleBar.setTvRightTextClickListener(lis);
        }

    }

//    private void getLoginData() {
//        String json = SharedPreferencesUtil.getInstance()
//                .getString(SharedPreferencesUtil.KEY_LOGIN_USER_INFO, "");
//        try {
//            if (!TextUtils.isEmpty(json)) {
//                loginInfo = gson.fromJson(json, Login.class);
//            } else {
//                loginInfo = null;
//            }
//
//        } catch (Exception e) {
//            LogUtil.LogE(BaseActivity.class, json);
//            CommonUtil2.clearLoginData();
//            loginInfo = null;
//        }
//        userId = SharedPreferencesUtil.getInstance()
//                .getString(SharedPreferencesUtil.KEY_LOGIN_USER_ID, "");
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
        ContextHelper.removeActivity(getClass());
        System.gc();
    }

    public <T extends Service> T getService(Class<T> tClass) {
        return Service.getService(tClass);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ContextHelper.addActivity(this);
    }

    public void onResume() {
        super.onResume();
        isOnResume = true;
        ContextHelper.setLastActivity(this);
        MobclickAgent.onResume(this);//友盟统计
        //getLoginData();
    }

//    public boolean isLogin() {
//        return loginInfo != null;
//    }

    public void onPause() {
        super.onPause();
        isOnResume = false;
        MobclickAgent.onPause(this);//友盟统计
        //onPauseIsLogin = isLogin();
        //释放掉没有取消订阅的对象
        for (Subscription sub: mSubscription) {
            if (sub != null) sub.unsubscribe();
        }
    }

    /**添加到订阅管理器中**/
    public void addSubscription(Subscription subscription) {
        mSubscription.add(subscription);
    }

    /**移除订阅**/
    public void removeSubScription(Subscription subscription) {
        subscription.unsubscribe();
        mSubscription.remove(subscription);
    }
//    // 更新 用户头像
//    public void upDataUserAvatar(String userAvatar) {
//        if (loginInfo != null) {
//            loginInfo.userAvatar = userAvatar;
//            SharedPreferencesUtil.getInstance().putString(SharedPreferencesUtil.KEY_LOGIN_USER_INFO, gson.toJson(loginInfo));
//        }
//    }
//
//    // 更新 用户 userName
//    public void upDataUserName(String userName) {
//        if (loginInfo != null) {
//            loginInfo.userName = userName;
//            SharedPreferencesUtil.getInstance().putString(SharedPreferencesUtil.KEY_LOGIN_USER_INFO, gson.toJson(loginInfo));
//        }
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (dispatchTouchEvent) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    hideKeyboard(v.getWindowToken());
                }
            }
            if (CommonUtils.isFastDoubleClick(0.5)) {
                LogUtils.LogE(BaseActivity.class, "dispatchTouchEvent ---> isFastDoubleClick");
                return true;
            }
            LogUtils.LogE(BaseActivity.class, "dispatchTouchEvent ---> MotionEvent.ACTION_DOWN");
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //禁止修改APP内部文字大小
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;

    }

    //处理 请求失败逻辑
    public void setHttpFailed(Activity activity, ResultBean resultBean){
        if("2002".equals(resultBean.code)){
            SPUtil.setLoginExit();
            CommonUtil.goToLogin(activity);
        }else{
            TipsToast.showTips(resultBean.message);
        }
    }

    private void initLangeuage() {
        String lan = LanguageUtils.getUserLanguageSetting();//读取语言设置
        LogUtils.LogD(MyApplication.class, "================之前选择的语言 : " + lan);
        LanguageUtils.saveLanguageSetting(LanguageUtils.getLocalFromCustomLang(lan));
    }


//    public void openLoginActicity(ResultBean resultBean) {
//        openLoginActicity(resultBean, true);
//
//    }

//    public void openLoginActicity(ResultBean resultBean, boolean showToast) {
//        if (resultBean == null) return;
//        if (resultBean.code == 4002) {// token 过期
//            CommonUtil2.clearLoginData();
//            CommonUtil2.goToLogin(mActivity);
//        }
//        if (showToast)
//            showToast(resultBean.resultMsg);
//
//    }


}

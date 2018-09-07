package com.lend.lendchain.ui.fragment;

import android.support.v4.app.Fragment;

import rx.Subscription;

/**
 * Created by yangfan on 2017/2/17.
 * 封装Fragment的数据缓加载
 */

public abstract class BaseFragment extends Fragment {

    public Subscription subscription;
    /**
     * Fragment是否可见
     */
    protected boolean isVisible;

    /**
     * 标志位，标志已经初始化完成
     */
    protected boolean isPrepared;

    protected boolean onPauseIsLogin;// 记录页面暂停时的登录状态
    protected boolean isFirstOnResume = true;
    protected boolean isOnResume = true;
    protected boolean isCanRequest = true;// true 可以请求

    /**
     * 在这里实现Fragment数据的缓加载
     * 此方法会告诉Fragment的UI是否可见
     * 当可见的时候，再去加载数据
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            /**
             * Fragment可见时
             */
            isVisible = true;
            onVisible();
        } else {
            /**
             * Fragment不可见时
             * 不可见的时候可以做一些初始化之前的准备工作
             */
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见的时候执行的此方法
     */
    protected abstract void onVisible();

    /**
     * 不可见的时候执行的此方法
     */
    protected void onInvisible() {
    }


    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     * @param isVisibleToUser true：用户能看见当前Fragment；false：用户看不见当前Fragment
     */
    public void onVisibilityChangedToUser(boolean isVisibleToUser, String tag){
        if(isVisibleToUser){
//            MobclickAgent.onPageStart(tag);
        }else{
//            MobclickAgent.onPageEnd(tag);
        }
    }
}
package com.lend.lendchain.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.SparseArray;

import com.lend.lendchain.MyApplication;
import com.lend.lendchain.ui.activity.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by yangfan on 2016/11/29.
 * 全局上下文 弱引用类 app管理类
 */
public class ContextHelper {

    private static MyApplication mApp;

    private static SparseArray<WeakReference<BaseActivity>> mAct = new SparseArray<>();
    private static WeakReference<BaseActivity> mLast = null;

    public static void initWithApplication(MyApplication application) {
        mApp = application;
    }

    public static MyApplication getApplication() {
        return mApp;
    }

    /**
     * 设置最后的
     *
     * @param act
     */
    public static void setLastActivity(BaseActivity act) {
        if (mLast != null && mLast.get() == act) return;
        mLast = new WeakReference<>(act);
    }

    /**
     * 最后一个Activity
     *
     * @return
     */
    public static BaseActivity getLastActivity() {
        if (mLast == null) return null;
        return mLast.get();
    }

    /**
     * 添加act
     *
     * @param act
     * @param <T>
     */
    public static <T extends BaseActivity> void addActivity(T act) {
        int code = act.getClass().hashCode();
        mAct.put(code, new WeakReference<BaseActivity>(act));
    }

    /**
     * 获取act
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends BaseActivity> T getActivity(Class<T> tClass) {
        WeakReference<BaseActivity> ref = mAct.get(tClass.hashCode());
        return ref != null ? (T) ref.get() : null;
    }

    /**
     * 移除Act
     *
     * @param tClass
     * @param <T>
     */
    public static <T extends BaseActivity> void removeActivity(Class<T> tClass) {
        mAct.remove(tClass.hashCode());
    }

    /**
     * 移除并finish Activity
     *
     * @param tClass
     * @param <T>
     */
    public static <T extends BaseActivity> void finishActivity(Class<T> tClass) {
        if(mAct.get(tClass.hashCode())!=null){
            mAct.get(tClass.hashCode()).get().finish();
            mAct.remove(tClass.hashCode());
        }
    }

    /**
     * 移除并finish Activity
     * @param activity
     */
    public static void finishActivity(Activity activity) {
        if(activity != null&&mAct.get(activity.getClass().hashCode())!=null) {
            mAct.get(activity.getClass().hashCode()).get().finish();
            mAct.remove(activity.getClass().hashCode());
        }

    }
    /**
     * 移除finish所有 Activity
     */
    public static void finishAllActivity() {
        for(int i = 0; i < mAct.size(); i++) {
            WeakReference<BaseActivity> act=mAct.valueAt(i);
            act.get().finish();
        }
        mAct.clear();
    }

    /**
     * 获取acttivity集合
     * @return
     */
    public static SparseArray<WeakReference<BaseActivity>> getActArray(){
        return mAct;
    }


    /**
     * 退出app
     * @param context
     */
    public static void appExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService("activity");
            activityMgr.restartPackage(context.getPackageName());
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception var3) {
            ;
        }

    }
}

package com.lend.lendchain.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * APP管理类
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if(instance == null) {
            instance = new AppManager();
        }

        return instance;
    }

    public void addActivity(Activity activity) {
        if(activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    public Activity currentActivity() {
        return (Activity)activityStack.lastElement();
    }

    public void finishActivity() {
        Activity activity = (Activity)activityStack.lastElement();
        this.finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if(activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }

    }

    public void finishActivity(Class<?> cls) {
        Iterator var2 = activityStack.iterator();

        while(var2.hasNext()) {
            Activity activity = (Activity)var2.next();
            if(activity.getClass().equals(cls)) {
                this.finishActivity(activity);
            }
        }

    }

    public void finishAllActivity() {
        int i = 0;

        for(int size = activityStack.size(); i < size; ++i) {
            if(null != activityStack.get(i)) {
                ((Activity)activityStack.get(i)).finish();
            }
        }

        activityStack.clear();
    }

    public void appExit(Context context) {
        try {
            this.finishAllActivity();
            ActivityManager activityMgr = (ActivityManager)context.getSystemService("activity");
            activityMgr.restartPackage(context.getPackageName());
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception var3) {
            ;
        }

    }
}

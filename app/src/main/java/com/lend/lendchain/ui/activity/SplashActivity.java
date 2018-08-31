package com.lend.lendchain.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.lang.ref.WeakReference;

import rx.Observer;

public class SplashActivity extends BaseActivity {

    public static final int DEFAULT_SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CommonUtil2.jPushAlias(true, CommonUtil2.getHxImUserIdByAppUserId(CommonUtil2.getPhoneSign()));
//        CommonUtil2.queryCurrencyRelationList(true);

        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarColor(SplashActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(SplashActivity.this);
        initView();
//        if(SPUtil.isLogin()){
//            setLogin();//登陆后每次启动app自动登录
//        }
    }

    private void setLogin() {
//        String userName= SPUtil.getUserName();
//        String pwd=SPUtil.getUserPwd();
//        NetApi.login(ContextHelper.getApplication(), false, userName, pwd, new NetClient.RxObserver<ResultBean>() {
//            @Override
//            public void onSuccess(ResultBean login) {//更新数据
//                if(login==null)return;
//                if(login.isSuccess()){
//                    SPUtil.setToken(login.access_token);
//                    SPUtil.setUserName(userName);//存用户名
//                    SPUtil.setUserPwd(pwd);//存密码
//                    SPUtil.setLoginState(true);
//                }else{//退出登录状态
//                    SPUtil.setLoginExit();//清除登录数据
//                }
//            }
//        });
    }
    @Override
    public void initView() {
        //请求权限
        RxPermissions rxPermission = new RxPermissions(SplashActivity.this);
        rxPermission
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(Boolean args) {
                        new StaticHandler(SplashActivity.this, SplashActivity.this).sendEmptyMessageDelayed(0, DEFAULT_SPLASH_TIME);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

    }

    private static class StaticHandler extends Handler {
        private final WeakReference<Context> weakContext;
        private final WeakReference<SplashActivity> mParent;

        public StaticHandler(Context context, SplashActivity view) {
            weakContext = new WeakReference<>(context);
            mParent = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = weakContext.get();
            SplashActivity parent = mParent.get();
            if (null != context && null != parent) {
                parent.initGoTo();
                super.handleMessage(msg);
            }
        }
    }

    private void initGoTo() {
        int verId = (int) SPUtil.getInt(SPUtil.KEY_VERSIONID,0);
//        if (CommonUtil.getVersionId(SplashActivity.this) > verId) {
//            CommonUtil.openActicity(SplashActivity.this, GuidePageActivity.class,
//                    null, true);
//        } else {
            CommonUtil.openActicity(SplashActivity.this, MainActivity.class, null, true);
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

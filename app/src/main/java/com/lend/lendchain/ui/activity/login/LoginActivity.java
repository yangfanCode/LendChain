package com.lend.lendchain.ui.activity.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.Login;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.MainActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.PopUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_tvGoRegiest)
    TextView tvGoRegiest;
    @BindView(R.id.login_tvForgetPwd)
    TextView tvForgetPwd;
    @BindView(R.id.login_btnLogin)
    TextView btnLogin;
    @BindView(R.id.login_etPwd)
    EditText etPwd;
    @BindView(R.id.login_etEmail)
    EditText etEmail;
    @BindView(R.id.login_ivSlices)
    ImageView ivSlices;
    @BindView(R.id.login_ivClose)
    ImageView ivClose;
    private PopupWindow popupWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        etPwd.setTypeface(Typeface.DEFAULT);
        etPwd.setTransformationMethod(new PasswordTransformationMethod());//修复inputType="textPassword" 时hint字体改变
        FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) ivClose.getLayoutParams();
        int statusBar=CommonUtil.getStatusBarHeight();
        params.topMargin=statusBar+ DisplayUtil.dp2px(this,23f);
        ivClose.setLayoutParams(params);
        initListener();
    }

    private void initListener() {
        ivClose.setOnClickListener(v -> finish());
        tvGoRegiest.setOnClickListener(v -> {//去注册
            CommonUtil.openActicity(LoginActivity.this, RegiestActivity.class, null);
        });
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = etEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email))return;
                if (!email.contains("@")) {
                    TipsToast.showTips(getString(R.string.please_input_correct_email));//邮箱不正确
                }
            }
        });
        ivSlices.setOnClickListener(v -> {
            if (popupWindow == null) {
                popupWindow = PopUtils.createPop(LoginActivity.this, R.layout.pwd_tip_layout);
            }
            ImageView iv=popupWindow.getContentView().findViewById(R.id.tip_iv);
            int width= ViewUtils.getViewWidth(iv);
            int height=ViewUtils.getViewHeight(iv);
            int[] location = new int[2];
            ivSlices.getLocationOnScreen(location);
            //x 屏幕宽度-popwindow宽度-margin  y ivSlices纵坐标-popwindow高度
            popupWindow.showAtLocation(ivSlices, Gravity.TOP | Gravity.LEFT, CommonUtils.getScreenWidth(LoginActivity.this) - width-DisplayUtil.dp2px(LoginActivity.this,22), location[1] - height);//展示按钮上方
        });
        btnLogin.setOnClickListener(v -> {
            //友盟埋点 立即登录
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_LOGIN);
            String userName = etEmail.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                TipsToast.showTips(getString(R.string.please_input_correct_email));
                return;
            }

            if (!CommonUtil.checkPwd(pwd)) {
                TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确密码
                return;
            }
            //立即登录
            NetApi.login(LoginActivity.this, true, userName, pwd, loginObserver);
        });
        tvForgetPwd.setOnClickListener(v -> {
            CommonUtil.openActicity(LoginActivity.this,ForgetPwdActivity.class,null);
        });
    }

    Observer<ResultBean<Login>> loginObserver = new NetClient.RxObserver<ResultBean<Login>>() {
        @Override
        public void onSuccess(ResultBean<Login> login) {
            if (login == null) return;
            if (login.isSuccess()) {
                //友盟埋点 登录成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_LOGIN_SUCCESS);
                NetApi.getUserInfo(LoginActivity.this,false,login.access_token,userInfoObserver);
                TipsToast.showTips(getString(R.string.login_success));
                SPUtil.setToken(login.access_token);//存token
                SPUtil.setEmail(login.data.identif.email);//存邮箱
//                SPUtil.setUserName(etEmail.getText().toString().trim());//存用户名
//                SPUtil.setUserPwd(etPwd.getText().toString().trim());//存密码
                SPUtil.setLoginState(true);

                ContextHelper.getApplication().runDelay(new Runnable() {
                    @Override
                    public void run() {
//                        setResult(Constant.RESULT_CODE1, new Intent());
//                        finish();
                        Bundle bundle=new Bundle();
                        bundle.putString(Constant.ARGS_PARAM1,Constant.LOGIN);
                        CommonUtil.openActicity(LoginActivity.this, MainActivity.class,bundle);
                    }
                }, 500);//延时关闭
            }else{
                TipsToast.showTips(login.message);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
    //保存用户认证信息
    Observer<ResultBean<UserInfo>> userInfoObserver=new NetClient.RxObserver<ResultBean<UserInfo>>() {
        @Override
        public void onSuccess(ResultBean<UserInfo> userInfoResultBean) {
            if(userInfoResultBean==null)return;
            if(userInfoResultBean.isSuccess()){
                SPUtil.setUserPhone(userInfoResultBean.data.identif.phone);//存手机认证状态
                SPUtil.setUserGoogle(userInfoResultBean.data.identif.google);//存谷歌认证状态
                SPUtil.setUserKyc(userInfoResultBean.data.identif.kyc);//存kyc认证状态
            }
        }
    };

}

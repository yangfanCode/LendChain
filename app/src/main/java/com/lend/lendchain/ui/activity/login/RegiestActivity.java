package com.lend.lendchain.ui.activity.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.verificationsdk.ui.IActivityCallback;
import com.alibaba.verificationsdk.ui.VerifyActivity;
import com.alibaba.verificationsdk.ui.VerifyType;
import com.lend.lendchain.R;
import com.lend.lendchain.bean.Login;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.MainActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.PopUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.AccountValidatorUtil;
import com.yangfan.utils.CommonUtils;
import com.yangfan.utils.CountDownTimer;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class RegiestActivity extends BaseActivity {

    @BindView(R.id.regiest_tvSendVerifyCode)
    TextView tvSendVerifyCode;
    @BindView(R.id.regiest_tvGoLogin)
    TextView tvGoLogin;
    @BindView(R.id.regiest_etEmail)
    EditText etEmail;
    @BindView(R.id.regiest_ivSlices)
    ImageView ivSlices;
    @BindView(R.id.regiest_etPwd)
    EditText etPwd;
    @BindView(R.id.regiest_etEmailCode)
    EditText etEmailCode;
    @BindView(R.id.regiest_etInviteCode)
    EditText etInviteCode;
    @BindView(R.id.regiest_btnRegiest)
    TextView btnRegiest;
    @BindView(R.id.regiest_ivClose)
    ImageView ivClose;
    @BindView(R.id.regiest_ivService)
    ImageView ivService;
    private CountDownTimer timer;
    private PopupWindow popupWindow=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows=false;
        setContentView(R.layout.activity_regiest);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        etPwd.setTypeface(Typeface.DEFAULT);
        etPwd.setTransformationMethod(new PasswordTransformationMethod());//修复inputType="textPassword" 时hint字体改变
        FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) ivClose.getLayoutParams();
        FrameLayout.LayoutParams params1= (FrameLayout.LayoutParams) ivService.getLayoutParams();
        int statusBar=CommonUtil.getStatusBarHeight();
        params.topMargin=statusBar+ DisplayUtil.dp2px(this,23f);
        params1.topMargin=statusBar+ DisplayUtil.dp2px(this,23f);
        ivClose.setLayoutParams(params);
        ivService.setLayoutParams(params1);
        initListener();
    }

    private void initListener() {
        ivClose.setOnClickListener(v -> finish());
        ivService.setOnClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        tvSendVerifyCode.setOnClickListener(v -> {
            String email=etEmail.getText().toString().trim();
            if(AccountValidatorUtil.isEmail(email)){
                //投篮验证
                VerifyActivity.startSimpleVerifyUI(RegiestActivity.this, VerifyType.NOCAPTCHA,"0335",null,iActivityCallback);
            }else{
                TipsToast.showTips(getString(R.string.please_input_correct_email));
            }
        });
        ivSlices.setOnClickListener(v -> {
            if (popupWindow == null) {
                popupWindow = PopUtils.createPop(RegiestActivity.this, R.layout.pwd_tip_layout);
            }
            ImageView iv=popupWindow.getContentView().findViewById(R.id.tip_iv);
            int width= ViewUtils.getViewWidth(iv);
            int height=ViewUtils.getViewHeight(iv);
            int[] location = new int[2];
            ivSlices.getLocationOnScreen(location);
            //x 屏幕宽度-popwindow宽度-margin  y ivSlices纵坐标-popwindow高度
            popupWindow.showAtLocation(ivSlices, Gravity.TOP | Gravity.LEFT, CommonUtils.getScreenWidth(RegiestActivity.this) - width-DisplayUtil.dp2px(RegiestActivity.this,22), location[1] - height);//展示按钮上方
        });
        etPwd.setOnFocusChangeListener((v, hasFocus) -> {//密码校验
            if(!hasFocus){
                String pwd=etPwd.getText().toString().trim();
                if(TextUtils.isEmpty(pwd))return;
                if(!CommonUtil.checkPwd(pwd)){
                    TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确密码
                }
            }
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
        btnRegiest.setOnClickListener(v -> {
            //友盟埋点 点击注册
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_SINGUP);
            //立即注册
            String email=etEmail.getText().toString().trim();
            String pwd=etPwd.getText().toString().trim();
            String emailCode=etEmailCode.getText().toString().trim();
            String invitedCode=etInviteCode.getText().toString().trim();
            if(!CommonUtil.checkPwd(pwd)){
                TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确密码
                return;
            }
            if(TextUtils.isEmpty(emailCode)){
                TipsToast.showTips(getString(R.string.please_input_email_code));
                return;
            }
            NetApi.regiest(RegiestActivity.this,false,email,emailCode,pwd,invitedCode,regiestObserver);
        });
        tvGoLogin.setOnClickListener(v -> {
            CommonUtil.openActicity(RegiestActivity.this,LoginActivity.class,null);
        });
    }
    //倒计时
    private void initTimer(){
        tvSendVerifyCode.setEnabled(false);
        timer=new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSendVerifyCode.setText(timer.getMillisUntilSecond(millisUntilFinished)+"s");
            }

            @Override
            public void onFinish() {
                tvSendVerifyCode.setEnabled(true);
                tvSendVerifyCode.setText(getString(R.string.send_verify_code));
                if(timer!=null){
                    timer.cancel();//置空
                    timer=null;//计时结束释放资源,防止对个对象计时不准
                }
            }
        };
    }

    /**
     * 验签回调功能函数
     * @param retInt 返回码
     * @param code 返回内容
     */
    public void verifyDidFinishedWithResult(int retInt, Map code) {
        switch (retInt) {
            case VerifyActivity.VERIFY_SUCC:
                Log.e("VERIFY_SUCC", (String) code.get("sessionID"));
                String sessionid = (String) code.get("sessionID");
                String email=etEmail.getText().toString().trim();
                //验证sessionid
                NetApi.sendEmailCode(RegiestActivity.this,true,email,sessionid,sendEmailCodeObserver);
                break;

            case VerifyActivity.VERIFY_FAILED:
//                Log.e(LOG_TAG, (String) code.get("errorCode"));
//                Log.e(LOG_TAG, (String) (null != code.get("errorMsg") ? code.get("errorMsg") : ""));
//                setLogMessage((String) (null != code.get("errorMsg") ? code.get("errorMsg") : ""));
                TipsToast.showTips((String) code.get("errorMsg"));
                VerifyActivity.finishVerifyUI();
                break;
        }
    }
    //发送邮箱验证码
    Observer<ResultBean> sendEmailCodeObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                initTimer();
                timer.start();//开启倒计时
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };
    //立即注册
    Observer<ResultBean> regiestObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                //友盟埋点 注册成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.MINE_SINGUP_SUCCESS);
                String email=etEmail.getText().toString().trim();
                String pwd=etPwd.getText().toString().trim();
                NetApi.login(RegiestActivity.this, true, email, pwd, loginObserver);
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };


    //投篮验证回调
    IActivityCallback iActivityCallback=new IActivityCallback() {
        @Override
        public void onNotifyBackPressed() {

        }

        @Override
        public void onResult(int i, Map<String, String> map) {
            verifyDidFinishedWithResult(i, map);
        }
    };

    Observer<ResultBean<Login>> loginObserver = new NetClient.RxObserver<ResultBean<Login>>() {
        @Override
        public void onSuccess(ResultBean<Login> login) {
            if (login == null) return;
            if (login.isSuccess()) {
                NetApi.getUserInfo(RegiestActivity.this,false,login.access_token,userInfoObserver);
                TipsToast.showTips(getString(R.string.regiest_success));
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
                        CommonUtil.openActicity(RegiestActivity.this, MainActivity.class,bundle);
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

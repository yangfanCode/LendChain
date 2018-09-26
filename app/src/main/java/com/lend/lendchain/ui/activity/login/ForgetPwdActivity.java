package com.lend.lendchain.ui.activity.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.PopUtils;
import com.lend.lendchain.utils.ViewUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.AccountValidatorUtil;
import com.yangfan.utils.CommonUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.forget_tvSendVerifyCode)
    TextView tvSendVerifyCode;
    @BindView(R.id.forget_tvGoRegiest)
    TextView tvGoRegiest;
    @BindView(R.id.forget_etEmail)
    EditText etEmail;
    @BindView(R.id.forget_ivSlices)
    ImageView ivSlices;
    @BindView(R.id.forget_etPwd)
    EditText etPwd;
    @BindView(R.id.forget_etEmailCode)
    EditText etEmailCode;
    @BindView(R.id.forget_btnforget)
    TextView btnConfirm;
    @BindView(R.id.forget_ivClose)
    ImageView ivClose;
    @BindView(R.id.forget_ivService)
    ImageView ivService;
    private CountDownTimer timer;
    private PopupWindow popupWindow=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;
        setContentView(R.layout.activity_forget_pwd);
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
                VerifyActivity.startSimpleVerifyUI(ForgetPwdActivity.this, VerifyType.NOCAPTCHA,"0335",null,iActivityCallback);
            }else{
                TipsToast.showTips(getString(R.string.please_input_correct_email));
            }
        });
        ivSlices.setOnClickListener(v -> {
            if (popupWindow == null) {
                popupWindow = PopUtils.createPop(ForgetPwdActivity.this, R.layout.pwd_tip_layout);
            }
            ImageView iv=popupWindow.getContentView().findViewById(R.id.tip_iv);
            int width= ViewUtils.getViewWidth(iv);
            int height=ViewUtils.getViewHeight(iv);
            int[] location = new int[2];
            ivSlices.getLocationOnScreen(location);
            //x 屏幕宽度-popwindow宽度-margin  y ivSlices纵坐标-popwindow高度
            popupWindow.showAtLocation(ivSlices, Gravity.TOP | Gravity.LEFT, CommonUtils.getScreenWidth(ForgetPwdActivity.this) - width-DisplayUtil.dp2px(ForgetPwdActivity.this,22), location[1] - height);//展示按钮上方
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
        btnConfirm.setOnClickListener(v -> {
            //立即注册
            String email=etEmail.getText().toString().trim();
            String pwd=etPwd.getText().toString().trim();
            String emailCode=etEmailCode.getText().toString().trim();
            if(!CommonUtil.checkPwd(pwd)){
                TipsToast.showTips(getString(R.string.please_input_correct_pwd));//请输入正确密码
                return;
            }
            if(TextUtils.isEmpty(emailCode)){
                TipsToast.showTips(getString(R.string.please_input_email_code));
                return;
            }
            NetApi.resetPwd(ForgetPwdActivity.this,true,email,emailCode,pwd,resetPwdObserver);
        });
        tvGoRegiest.setOnClickListener(v -> {
            CommonUtil.openActicity(ForgetPwdActivity.this,RegiestActivity.class,null);
        });
    }

    //倒计时
    private void initTimer(){
        tvSendVerifyCode.setEnabled(false);
        timer=new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSendVerifyCode.setText(millisUntilFinished/1000+"s");
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
                NetApi.sendEmailCode(ForgetPwdActivity.this,true,email,sessionid,sendEmailCodeObserver);
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
    Observer<ResultBean> resetPwdObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.update_success));
                ContextHelper.getApplication().runDelay(ForgetPwdActivity.this::finish,500);//延时关闭页面
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

}

package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.QRCodeUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;
import com.yangfan.utils.CountDownTimer;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 谷歌认证
 */
public class GoogleCertifyActivity extends BaseActivity {
    @BindView(R.id.google_certify_etSmsCode)
    EditText etSmsCode;
    @BindView(R.id.google_certify_etGoogleCode)
    EditText etgoogleCode;
    @BindView(R.id.google_certify_tvSendSmsCode)
    TextView tvSendSmsCode;
    @BindView(R.id.google_certify_tvCopy)
    TextView tvCopy;
    @BindView(R.id.google_certify_tvSecret)
    TextView tvSecret;
    @BindView(R.id.google_certify_ivQRCode)
    ImageView ivQRCode;
    @BindView(R.id.google_certify_btnConfirm)
    TextView btnConfirm;
    private CountDownTimer timer;
    private String secret;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_certify);
        StatusBarUtil.setStatusBarColor(GoogleCertifyActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(GoogleCertifyActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.google_certified));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        initData();
        initListener();
    }


    private void initData() {
        NetApi.getGoogleCode(GoogleCertifyActivity.this,true, SPUtil.getToken(),googleCodeObserver);
    }

    private void initListener() {
        tvSendSmsCode.setOnClickListener(v -> {
            //友盟埋点 提交验证码
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_SUBMIT_GOOGLECODE);
            NetApi.sendSMSCodeNoPhone(GoogleCertifyActivity.this,true,SPUtil.getToken(),sendSmsCodeObserver);
        });
        tvCopy.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(secret)){
                CommonUtil.clipboardStr(secret);
                TipsToast.showTips(getString(R.string.copy_success));
            }
        });
        btnConfirm.setOnClickListener(v -> {
            String googleCode=etgoogleCode.getText().toString().trim();
            String smsCode=etSmsCode.getText().toString().trim();
            if(!TextUtils.isEmpty(smsCode)){
                if(!TextUtils.isEmpty(googleCode)){//谷歌认证
                    NetApi.googleCertfy(GoogleCertifyActivity.this,true,SPUtil.getToken(),googleCode,smsCode,googleCertifyObserver);
                }else{
                    TipsToast.showTips(getString(R.string.please_input_google));//请输入谷歌验证码
                }
            }else{
                TipsToast.showTips(getString(R.string.please_input_smscode));//请输入验证码
            }

        });
    }
    //倒计时
    private void initTimer(){
        tvSendSmsCode.setEnabled(false);
        timer=new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSendSmsCode.setText(timer.getMillisUntilSecond(millisUntilFinished)+"s");
            }

            @Override
            public void onFinish() {
                tvSendSmsCode.setEnabled(true);
                tvSendSmsCode.setText(getString(R.string.send_verify_code));
                if(timer!=null){
                    timer.cancel();//置空
                    timer=null;//计时结束释放资源,防止对个对象计时不准
                }
            }
        };
    }
    Observer<ResultBean<SimpleBean>> googleCodeObserver=new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                secret=resultBean.data.secret;
                //生成二维码
                String s="otpauth://totp/"+SPUtil.getEmail()+"?secret="+secret+"&issuer=lendx.vip";
                ivQRCode.setImageBitmap(QRCodeUtil.createQrcode(s, DisplayUtil.dp2px(GoogleCertifyActivity.this,80),0));
                tvSecret.setText(secret);
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };
    Observer<ResultBean> sendSmsCodeObserver=new NetClient.RxObserver<ResultBean>() {
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
    Observer<ResultBean> googleCertifyObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                //友盟埋点 谷歌绑定成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_GOOGLE_BINDED_SUCCESSFUL);
                TipsToast.showTips(getString(R.string.certified_success));
                SPUtil.setUserGoogle(1);//存谷歌认证状态
                ContextHelper.getApplication().runDelay(GoogleCertifyActivity.this::finish,500);//延时关闭
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };
}

package com.lend.lendchain.ui.activity.account.rechargewithdraw;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.MyWalletActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;
import com.yangfan.utils.CountDownTimer;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class TransferCertifyActivity extends BaseActivity {


    @BindView(R.id.transfer_certify_etEmailCode)
    EditText etEmailCode;
    @BindView(R.id.transfer_certify_etGoogleCode)
    EditText etGoogleCode;
    @BindView(R.id.transfer_certify_tvSendCode)
    TextView tvSendCode;
    @BindView(R.id.transfer_certify_tvPaste)
    TextView tvPaste;
    @BindView(R.id.transfer_certify_btnConfirm)
    Button btnConfirm;
    private CountDownTimer timer;
    private String count,cryptoId,cryptoCode,email,nickname,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_certify);
        StatusBarUtil.setStatusBarColor(TransferCertifyActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(TransferCertifyActivity.this);
    }
    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.transfer_cerfity));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        count=getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        cryptoId=getIntent().getExtras().getString(Constant.ARGS_PARAM2);
        cryptoCode=getIntent().getExtras().getString(Constant.ARGS_PARAM3);
        email=getIntent().getExtras().getString(Constant.ARGS_PARAM4);
        nickname=getIntent().getExtras().getString(Constant.ARGS_PARAM5);
        phone=getIntent().getExtras().getString(Constant.ARGS_PARAM6);
        initListener();
    }

    private void initListener() {
        tvPaste.setOnClickListener(v -> {
            String str= CommonUtil.clipboardPasteStr();
            if(!TextUtils.isEmpty(str)){
                etGoogleCode.setText(str);
            }
        });
        tvSendCode.setOnClickListener(v -> {
            NetApi.sendEmailCodeNoEmail(TransferCertifyActivity.this,true, SPUtil.getToken(),sendEmailCodeObserver);
        });
        btnConfirm.setOnClickListener(v -> {
            String googleCode=etGoogleCode.getText().toString().trim();
            String emailCode=etEmailCode.getText().toString().trim();
            if(TextUtils.isEmpty(emailCode)){
                TipsToast.showTips(getString(R.string.please_input_email_code));
                return;
            }
            if(TextUtils.isEmpty(googleCode)){
                TipsToast.showTips(getString(R.string.please_input_google_code));
                return;
            }
            NetApi.transferCreate(TransferCertifyActivity.this,true,SPUtil.getToken(),cryptoId,cryptoCode,googleCode,email,count,nickname,emailCode,phone,transferObserver);
        });
    }
    //倒计时
    private void initTimer(){
        tvSendCode.setEnabled(false);
        timer=new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSendCode.setText(timer.getMillisUntilSecond(millisUntilFinished)+"s");
            }

            @Override
            public void onFinish() {
                tvSendCode.setEnabled(true);
                tvSendCode.setText(getString(R.string.send_verify_code));
                if(timer!=null){
                    timer.cancel();//置空
                    timer=null;//计时结束释放资源,防止对个对象计时不准
                }
            }
        };
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
    //提现
    Observer<ResultBean> transferObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.transfer_success));
                CommonUtil.openActivityWithLogin(TransferCertifyActivity.this,MyWalletActivity.class,null);
            }else{
                setHttpFailed(TransferCertifyActivity.this,resultBean);
            }
        }
    };
}

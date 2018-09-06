package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.singleton.CountryCode;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.SelectCountryCodeActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.TipsToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class PhoneCertifyActivity extends BaseActivity {

    @BindView(R.id.phone_certify_tvSendSmsCode)
    TextView tvSendSmsCode;
//    @BindView(R.id.phone_certify_tvCountryCode)
//    TextView tvCountryCode;
    @BindView(R.id.phone_certify_etSmsCode)
    EditText etSmsCode;
    @BindView(R.id.phone_certify_etPhone)
    EditText etPhone;
    @BindView(R.id.phone_certify_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.phone_certify_tvCountryCodeDesc)
    TextView tvCountryCodeDesc;
    private CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_certify);
        StatusBarUtil.setStatusBarColor(PhoneCertifyActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(PhoneCertifyActivity.this);
    }
    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setTitle(getString(R.string.phone_certified));
        //默认国家码
        tvCountryCodeDesc.setText(CountryCode.getCountryName()+" "+CountryCode.getCountryCode());
        initListener();
    }

    private void initListener() {
        tvCountryCodeDesc.setOnClickListener(v -> CommonUtil.openActicity(PhoneCertifyActivity.this, SelectCountryCodeActivity.class,null));
        tvSendSmsCode.setOnClickListener(v -> {
            //友盟埋点 提交验证码
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_SUBMIT_SMSCODE);
            String phone=etPhone.getText().toString().trim();
            String countryCode=CountryCode.getCountryCode();
            if(!TextUtils.isEmpty(countryCode)){
                if(!TextUtils.isEmpty(phone)){
                    if(phone.length()>6){
                        NetApi.sendSMSCode(PhoneCertifyActivity.this,true, SPUtil.getToken(),phone,countryCode.replace("+",""),sendSMSObserver);
                    }else{
                        TipsToast.showTips(getString(R.string.please_input_correct_phone));
                    }
                }
            }else{
                TipsToast.showTips(getString(R.string.please_select_country_area));
            }
        });
        btnConfirm.setOnClickListener(v -> {
            //友盟埋点 点击绑定
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_PHONE_BIND);
            String phone=etPhone.getText().toString().trim();
            String countryCode=CountryCode.getCountryCode();
            String code=etSmsCode.getText().toString().trim();
            if(TextUtils.isEmpty(phone)){
                TipsToast.showTips(getString(R.string.please_input_phone));
                return;
            }
            if(TextUtils.isEmpty(code)){
                TipsToast.showTips(getString(R.string.please_input_smscode));
                return;
            }
            NetApi.phoneCertify(PhoneCertifyActivity.this,true,SPUtil.getToken(),phone,code,countryCode.replace("+",""),phoneCertifyObserver);
        });
    }
    //倒计时
    private void initTimer(){
        tvSendSmsCode.setEnabled(false);
        timer=new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSendSmsCode.setText(millisUntilFinished/1000+"s");
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
    @Override
    protected void onRestart() {
        super.onRestart();
        //更新国家码
        tvCountryCodeDesc.setText(CountryCode.getCountryName()+" "+CountryCode.getCountryCode());
    }

    Observer<ResultBean> sendSMSObserver=new NetClient.RxObserver<ResultBean>() {
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
    Observer<ResultBean> phoneCertifyObserver=new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                //友盟埋点 手机绑定成功
                UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_PHONE_BINDED_SUCCESSFUL);
                TipsToast.showTips(getString(R.string.certified_success));
                SPUtil.setUserPhone(1);//存手机认证状态
                ContextHelper.getApplication().runDelay(PhoneCertifyActivity.this::finish,500);//延时关闭
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };
}

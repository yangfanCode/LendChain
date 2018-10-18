package com.lend.lendchain.ui.activity.account.certify;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.UserInfo;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 安全认证页面 到这个页面那肯定是登陆的
 */

public class SafeCertifyActivity extends BaseActivity {


    @BindView(R.id.safe_certify_tvPhone)
    TextView tvPhone;
    @BindView(R.id.safe_certify_tvGoogle)
    TextView tvGoogle;
    @BindView(R.id.safe_certify_tvKyc)
    TextView tvKyc;
    @BindView(R.id.safe_certify_tvRefuse)
    TextView tvRefuse;
    private Dialog dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_certify);
        StatusBarUtil.setStatusBarColor(SafeCertifyActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(SafeCertifyActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setTitle(getString(R.string.safe_certified));
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        NetApi.getUserInfo(SafeCertifyActivity.this,true, SPUtil.getToken(),userInfoObserver);
        initListener();
    }

    private void initListener() {
        tvPhone.setOnClickListener(v -> {
            //友盟埋点 点击手机认证
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_PHONE_VERIFY);
            CommonUtil.openActicity(SafeCertifyActivity.this,PhoneCertifyActivity.class,null);
        });
        //谷歌认证之前要先认证手机
        tvGoogle.setOnClickListener(v -> {
            //友盟埋点 点击谷歌认证
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_GOOGLE_VERIFY);
            if(SPUtil.getUserPhone()){
                CommonUtil.openActicity(SafeCertifyActivity.this,GoogleCertifyActivity.class,null);
            }else{
                TipsToast.showTips(getString(R.string.please_cerfity_phone_first));
            }
        });
        tvKyc.setOnClickListener(v -> {
            //友盟埋点 点击kyc
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_KYC_VERIFY);
            CommonUtil.openActicity(SafeCertifyActivity.this,KycActivity.class,null);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //返回刷新数据
        NetApi.getUserInfo(SafeCertifyActivity.this,true, SPUtil.getToken(),userInfoObserver);
    }

    private void setKycText(int kycCerfity){
        String kycStr;
        if(kycCerfity==0){
            kycStr=getString(R.string.go_certified);
            tvKyc.setEnabled(true);
        }else if(kycCerfity==1){
            kycStr=getString(R.string.certifing_shenhe);
            tvKyc.setEnabled(false);
        }else if(kycCerfity==2){
            kycStr=getString(R.string.certified);
            tvKyc.setEnabled(false);
        }else{
            kycStr=getString(R.string.kyc_certify_retry);
            tvKyc.setEnabled(true);
            tvRefuse.setVisibility(View.VISIBLE);
            tvRefuse.setOnClickListener(v -> {
                NetApi.kycRefuseReason(SafeCertifyActivity.this,true,SPUtil.getToken(),kycRefuseObserver);
            });
        }
        tvKyc.setText(kycStr);
    }

    //保存用户认证信息
    Observer<ResultBean<UserInfo>> userInfoObserver=new NetClient.RxObserver<ResultBean<UserInfo>>() {
        @Override
        public void onSuccess(ResultBean<UserInfo> userInfoResultBean) {
            if(userInfoResultBean==null)return;
            if(userInfoResultBean.isSuccess()){
                boolean isGoCerifyPhone=userInfoResultBean.data.identif.phone==0;//是否手机认证
                boolean isGoCerifyGoogle=userInfoResultBean.data.identif.google==0;//是否谷歌认证
                int kycCerfity=userInfoResultBean.data.identif.kyc;//kyc认证
                tvPhone.setEnabled(isGoCerifyPhone);
                tvGoogle.setEnabled(isGoCerifyGoogle);
                tvPhone.setText(isGoCerifyPhone?getString(R.string.go_certified):getString(R.string.certified));
                tvGoogle.setText(isGoCerifyGoogle?getString(R.string.go_certified):getString(R.string.certified));
                setKycText(kycCerfity);
                SPUtil.setUserPhone(userInfoResultBean.data.identif.phone);//存手机认证状态
                SPUtil.setUserGoogle(userInfoResultBean.data.identif.google);//存谷歌认证状态
                SPUtil.setUserKyc(userInfoResultBean.data.identif.kyc);//存kyc认证状态
            }
        }
    };

    Observer<ResultBean<String>> kycRefuseObserver=new NetClient.RxObserver<ResultBean<String>>() {
        @Override
        public void onSuccess(ResultBean<String> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                CustomDialog.Builder builder = new CustomDialog.Builder(SafeCertifyActivity.this);
                builder.setTitle(getString(R.string.kyc_refuse));
                builder.setMessage(resultBean.data);
                builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> dialog.dismiss());
                dialog=builder.create();
                builder.getNegativeButton().setVisibility(View.GONE);
                builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
                dialog.setCancelable(false);
                dialog.show();
            }else{
                TipsToast.showTips(resultBean.message);
            }
        }
    };
}

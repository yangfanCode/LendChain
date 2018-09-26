package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.FormNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class UserCenterActivity extends BaseActivity {

    @BindView(R.id.user_center_llHead)
    LinearLayout llHead;
    @BindView(R.id.user_center_tvUserName)
    TextView tvEmail;
    @BindView(R.id.user_center_fnPhone)
    FormNormal fnPhone;
    @BindView(R.id.user_center_fnNickName)
    FormNormal fnNickName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows=false;
        setContentView(R.layout.activity_user_center);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.user_center));
        baseTitleBar.getTvTitle().setTextColor(ColorUtils.WHITE);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        int height= CommonUtil.getStatusBarHeight();
        llHead.setPadding(0,height,0,0);
        initData();
    }

    private void initData() {
        NetApi.getUserInfo(this,true, SPUtil.getToken(),userInfoObserver);//请求用户信息
    }

    //保存用户认证信息
    Observer<ResultBean<UserInfo>> userInfoObserver=new NetClient.RxObserver<ResultBean<UserInfo>>() {
        @Override
        public void onSuccess(ResultBean<UserInfo> userInfoResultBean) {
            if(userInfoResultBean==null)return;
            if(userInfoResultBean.isSuccess()){
                SPUtil.setUserPhone(userInfoResultBean.data.identif.phone);//存手机认证状态
                SPUtil.setUserGoogle(userInfoResultBean.data.identif.google);//存谷歌认证状态
                SPUtil.setUserKyc(userInfoResultBean.data.identif.kyc);//存kyc认证状态
                tvEmail.setText(userInfoResultBean.data.email);//展示email
                String phone=userInfoResultBean.data.phone;
                String nikeName=userInfoResultBean.data.nickname;
                //设置手机号状态
                if(TextUtils.isEmpty(phone)){//无手机号
                    fnPhone.getImvIndicator().setVisibility(View.VISIBLE);
                    fnPhone.setImvIndicatorImageResource(R.mipmap.icon_jiantou_right);
                    fnPhone.setText(getString(R.string.go_set));//去设置
                    fnPhone.setOnClickListener(v -> {
                        //友盟埋点 点击手机认证
                        UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_PHONE_VERIFY);
                        CommonUtil.openActicity(UserCenterActivity.this,PhoneCertifyActivity.class,null);
                    });
                }else{
                    fnPhone.getImvIndicator().setVisibility(View.GONE);
                    fnPhone.setText(phone);
                }
                //设置昵称状态
                if(TextUtils.isEmpty(nikeName)){//无昵称
                    fnNickName.getImvIndicator().setVisibility(View.VISIBLE);
                    fnNickName.setImvIndicatorImageResource(R.mipmap.icon_jiantou_right);
                    fnNickName.setText(getString(R.string.go_set));//去设置
                    fnNickName.setOnClickListener(v -> CommonUtil.openActicity(UserCenterActivity.this,NickNameSetActivity.class,null));
                }else{
                    fnNickName.getImvIndicator().setVisibility(View.GONE);
                    fnNickName.setText(nikeName);
                }
            }
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }
}

package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.widget.DecimalDigitsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class PlatFormTranshferActivity extends BaseActivity {

    @BindView(R.id.transfer_etEmail)
    EditText etEmail;
    @BindView(R.id.transfer_etNickName)
    EditText etNickName;
    @BindView(R.id.transfer_etPhone)
    EditText etPhone;
    @BindView(R.id.transfer_etCount)
    DecimalDigitsEditText etCount;
    @BindView(R.id.transfer_btnConfirm)
    Button btnConfirm;
    @BindView(R.id.transfer_tvCode)
    TextView tvCode;
    private String cryptoId, cryptoCode;
    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plat_form_transhfer);
        StatusBarUtil.setStatusBarColor(PlatFormTranshferActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(PlatFormTranshferActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.transfer_platform));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        cryptoId = getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
        cryptoCode = getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        tvCode.setText(cryptoCode);
        initLisener();
    }

    private void initLisener() {
        btnConfirm.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String count = etCount.getText().toString().trim();
            if (!email.contains("@")) {
                TipsToast.showTips(getString(R.string.please_input_email));
                return;
            }
            if (TextUtils.isEmpty(count)) {
                TipsToast.showTips(getString(R.string.please_input_transfer_count));
                return;
            }
            //读取用户此币种余额 是否不足
            NetApi.getCoinCount(PlatFormTranshferActivity.this,false, SPUtil.getToken(), Integer.valueOf(cryptoId), getCoinCountObserver);
        });
    }
    Observer<ResultBean<SimpleBean>> getCoinCountObserver=new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null){
                    String count=etCount.getText().toString().trim();
                    double over=resultBean.data.amount;
                    if(Double.parseDouble(count)>over){
                        TipsToast.showTips(getString(R.string.over_lack));
                    }else{
                        String email = etEmail.getText().toString().trim();
                        String nickname = etNickName.getText().toString().trim();
                        String phone = etPhone.getText().toString().trim();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ARGS_PARAM1, count);
                        bundle.putString(Constant.ARGS_PARAM2, cryptoId);
                        bundle.putString(Constant.ARGS_PARAM3, cryptoCode);
                        bundle.putString(Constant.ARGS_PARAM4, email);
                        bundle.putString(Constant.ARGS_PARAM5, nickname);
                        bundle.putString(Constant.ARGS_PARAM6, phone);
                        CommonUtil.openActicity(PlatFormTranshferActivity.this, TransferCertifyActivity.class, bundle);
                    }
                }
            }else{
                setHttpFailed(PlatFormTranshferActivity.this,resultBean);
            }
        }
        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
}

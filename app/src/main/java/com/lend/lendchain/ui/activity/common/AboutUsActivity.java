package com.lend.lendchain.ui.activity.common;

import android.os.Bundle;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.network.NetConst;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.widget.FormNormal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {


    @BindView(R.id.abount_us_fnLendChain)
    FormNormal fnLendChain;
    @BindView(R.id.abount_us_fnVersionName)
    FormNormal fnVersionName;
    @BindView(R.id.abount_us_fnWeb)
    FormNormal fnWeb;
    @BindView(R.id.abount_us_fnWeChatAcount)
    FormNormal fnWeChatAcount;
    @BindView(R.id.abount_us_fnSinaWeibo)
    FormNormal fnSinaWeibo;
    @BindView(R.id.abount_us_fnTwitter)
    FormNormal fnTwitter;
    @BindView(R.id.abount_us_fnTelegram)
    FormNormal fnTelegram;
    @BindView(R.id.abount_us_fnFacebook)
    FormNormal fnFacebook;
    @BindView(R.id.abount_us_fnInstagram)
    FormNormal fnInstagram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        StatusBarUtil.setStatusBarColor(AboutUsActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(AboutUsActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setTitle(getString(R.string.about_us));
        initListener();
    }

    private void initListener() {
        fnLendChain.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.ABOUT_US+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnLendChain.getTvTitle().getText().toString().trim());
            CommonUtil.openActicity(AboutUsActivity.this,WebActivity.class,bundle);
        });
        fnVersionName.setText(CommonUtil.getVersionName(this));
        TextView tvWeb=fnWeb.getTextView();
        tvWeb.setPadding(5,5,5,5);
        tvWeb.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnWeb.getText());
            showTipSuccess();
        });
        TextView tvWeChatAcount=fnWeChatAcount.getTextView();
        tvWeChatAcount.setPadding(5,5,5,5);
        tvWeChatAcount.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnWeChatAcount.getText());
            showTipSuccess();
        });
        TextView tvSinaWeibo=fnSinaWeibo.getTextView();
        tvSinaWeibo.setPadding(5,5,5,5);
        tvSinaWeibo.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnSinaWeibo.getText());
            showTipSuccess();
        });
        TextView tvTwitter=fnTwitter.getTextView();
        tvTwitter.setPadding(5,5,5,5);
        tvTwitter.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnTwitter.getText());
            showTipSuccess();
        });
        TextView tvTelegram=fnTelegram.getTextView();
        tvTelegram.setPadding(5,5,5,5);
        tvTelegram.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnTelegram.getText());
            showTipSuccess();
        });
        TextView tvFacebook=fnFacebook.getTextView();
        tvFacebook.setPadding(5,5,5,5);
        tvFacebook.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnFacebook.getText());
            showTipSuccess();
        });
        TextView tvInstagram=fnInstagram.getTextView();
        tvInstagram.setPadding(5,5,5,5);
        tvInstagram.setOnClickListener(v -> {
            CommonUtil.clipboardStr(fnInstagram.getText());
            showTipSuccess();
        });
    }

    private void showTipSuccess(){
        TipsToast.showTips(getString(R.string.copy_success));
    }
}

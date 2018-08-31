package com.lend.lendchain.ui.activity.common;

import android.os.Bundle;

import com.lend.lendchain.R;
import com.lend.lendchain.network.NetConst;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.yangfan.widget.FormNormal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpCenterActivity extends BaseActivity {
    @BindView(R.id.help_center_fnRateTip)
    FormNormal fnRateTip;
    @BindView(R.id.help_center_fnRiskManage)
    FormNormal fnRiskManage;
    @BindView(R.id.help_center_fnNewbieGuide)
    FormNormal fnNewbieGuide;
    @BindView(R.id.help_center_fnHowToPlay)
    FormNormal fnHowToPlay;
    @BindView(R.id.help_center_fnMortgageDes)
    FormNormal fnMortgageDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        StatusBarUtil.setStatusBarColor(HelpCenterActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(HelpCenterActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.help_center));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        initListener();
    }

    private void initListener() {
        fnRateTip.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.RATE_TIPNS+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnRateTip.getTvTitle().getText().toString().toString());
            CommonUtil.openActicity(HelpCenterActivity.this,WebActivity.class,bundle);
        });
        fnRiskManage.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.RIST_MANAGE+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnRiskManage.getTvTitle().getText().toString().toString());
            CommonUtil.openActicity(HelpCenterActivity.this,WebActivity.class,bundle);
        });
        fnNewbieGuide.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.NEWBIE_GUIDE+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnNewbieGuide.getTvTitle().getText().toString());
            CommonUtil.openActicity(HelpCenterActivity.this,WebActivity.class,bundle);
        });
        fnHowToPlay.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.HOW_TO_PLAY_MORGTAGE+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnHowToPlay.getTvTitle().getText().toString());
            CommonUtil.openActicity(HelpCenterActivity.this,WebActivity.class,bundle);
        });
        fnMortgageDes.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_URL, NetConst.dynamicBaseUrlForH5()+NetConst.MORTGAGE_DESCRIPTION+ LanguageUtils.getUserLanguageSetting());
            bundle.putString(Constant.INTENT_EXTRA_TITLE,fnMortgageDes.getTvTitle().getText().toString());
            CommonUtil.openActicity(HelpCenterActivity.this,WebActivity.class,bundle);
        });
    }

}

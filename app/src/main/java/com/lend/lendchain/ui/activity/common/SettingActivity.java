package com.lend.lendchain.ui.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.MainActivity;
import com.lend.lendchain.ui.activity.login.UpdatePwdActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.TipsToast;
import com.lvfq.pickerview.OptionsPickerView;
import com.yangfan.widget.FormNormal;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_tvLoginOut)
    TextView tvLoginOut;
    @BindView(R.id.setting_fnUpdatePwd)
    FormNormal fnUpdatePwd;
    @BindView(R.id.setting_fnLangauge)
    FormNormal fnLangauge;
    private OptionsPickerView optionsPickerView=null;
    private ArrayList<String> languages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows=false;
        setContentView(R.layout.activity_setting);
        int height= CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0,height,0,0);
        StatusBarUtil.StatusBarLightMode(SettingActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setTitle(getString(R.string.setting));
        tvLoginOut.setVisibility(SPUtil.isLogin()? View.VISIBLE:View.GONE);
        languages=new ArrayList<>();
        languages.add(getString(R.string.chinese));//中文
        languages.add(getString(R.string.english));//英文
        languages.add(getString(R.string.korean));//韩文
        initListener();
    }

    private void initListener() {
        tvLoginOut.setOnClickListener(v -> {
            SPUtil.setLoginExit();
            TipsToast.showTips(getString(R.string.login_out_success));
            ContextHelper.getApplication().runDelay(new Runnable() {
                @Override
                public void run() {
                    setResult(Constant.RESULT_CODE2, new Intent());
                    finish();
                }
            }, 500);//延时关闭
        });
        fnUpdatePwd.setOnClickListener(v -> {
            CommonUtil.openActivityWithLogin(SettingActivity.this, UpdatePwdActivity.class,null);
        });
        fnLangauge.setOnClickListener(v -> {
            if(optionsPickerView==null){
                optionsPickerView = new OptionsPickerView(SettingActivity.this);
                optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                    fnLangauge.setText(languages.get(options1));
                    switchLanguage(languages.get(options1));
                });
                optionsPickerView.setCancelable(true);
                optionsPickerView.setPicker(languages);
                optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
                optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            }
            optionsPickerView.setSelectOptions(languages.indexOf(fnLangauge.getText()));
            optionsPickerView.show();
        });
    }
    //切换语言
    private void switchLanguage(String lang) {
        if(lang.equals(getString(R.string.chinese))){//切换中文
            LanguageUtils.saveLanguageSetting(Locale.SIMPLIFIED_CHINESE);
        }else if(lang.equals(getString(R.string.korean))){//切换韩文
            LanguageUtils.saveLanguageSetting(Locale.KOREAN);
        }else{//英文
            LanguageUtils.saveLanguageSetting(Locale.ENGLISH);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

}

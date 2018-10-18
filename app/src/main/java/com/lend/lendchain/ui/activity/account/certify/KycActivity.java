package com.lend.lendchain.ui.activity.account.certify;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.KycInfo;
import com.lend.lendchain.singleton.CountryCode;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.ui.activity.common.SelectCountryCodeActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.KeyBordUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.TipsToast;
import com.lvfq.pickerview.OptionsPickerView;
import com.lvfq.pickerview.TimePickerView;
import com.yangfan.utils.CommonUtils;
import com.yangfan.widget.FormNormal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KycActivity extends BaseActivity {

    @BindView(R.id.kyc_fnCountry)
    FormNormal fnCountry;
    @BindView(R.id.kyc_fnBornDate)
    FormNormal fnBornDate;
    @BindView(R.id.kyc_fnDocument)
    FormNormal fnDocument;
    @BindView(R.id.loan_etRealName)
    EditText etRealName;
    @BindView(R.id.kyc_etDocumentNum)
    EditText etDocumentNum;
    @BindView(R.id.kyc_realName_layout)
    FrameLayout layoutRealName;
    @BindView(R.id.kyc_document_layout)
    FrameLayout layoutDocumentNum;
    @BindView(R.id.kyc_btnNextStep)
    TextView btnNextStep;
    private ArrayList<String> listDocumentType;
    private String dateFormat = "yyyy.MM.dd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows=false;
        setContentView(R.layout.activity_kyc);
        int height= CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0,height,0,0);
        StatusBarUtil.StatusBarLightMode(KycActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.kyc_certify));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        listDocumentType=new ArrayList<>();
        listDocumentType.add(getString(R.string.id_card));
        listDocumentType.add(getString(R.string.passport));
        initData();
        initListener();
    }

    private void initData() {
        fnCountry.setText(getString(R.string.china).concat("+86"));
    }

    private void initListener() {
        layoutRealName.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(KycActivity.this, etRealName));
        layoutDocumentNum.setOnClickListener(v -> KeyBordUtils.setEditTextFocus(KycActivity.this, etDocumentNum));
        fnCountry.setOnClickListener(v -> CommonUtil.openActicity(KycActivity.this, SelectCountryCodeActivity.class,null));
        fnBornDate.setOnClickListener(v -> {
            KeyBordUtils.closeKeybord(KycActivity.this,etDocumentNum,etRealName);
            TimePickerView timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            timePickerView.setOnTimeSelectListener(date -> fnBornDate.setText(CommonUtils.date2String(date, dateFormat)));
            timePickerView.setCancelable(true);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int year = calendar.get(Calendar.YEAR);
            timePickerView.setRange(1900, year);
            String dateS = fnBornDate.getText();
            Date date;
            try {
                date = TextUtils.isEmpty(dateS) ? new Date() : (new SimpleDateFormat(dateFormat)).parse(dateS);
            } catch (ParseException e) {
                date = new Date();
            }
            timePickerView.setTime(date);
            timePickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            timePickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            timePickerView.show();
        });
        fnDocument.setOnClickListener(v -> {
            KeyBordUtils.closeKeybord(KycActivity.this,etDocumentNum,etRealName);
            OptionsPickerView optionsPickerView = new OptionsPickerView(KycActivity.this);
            optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> fnDocument.setText(listDocumentType.get(options1)));
            optionsPickerView.setCancelable(true);
            optionsPickerView.setPicker(listDocumentType);
            optionsPickerView.setSelectOptions(listDocumentType.indexOf(fnDocument.getText()));
            optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
            optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
            optionsPickerView.show();
        });
        btnNextStep.setOnClickListener(v -> {
            //友盟埋点 提交个人信息
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.SAFE_KYC_PERSONAL_SUBMIT);
            if(TextUtils.isEmpty(fnCountry.getText())){
                TipsToast.showTips(getString(R.string.please_select_country_code));
                return;
            }
            if(TextUtils.isEmpty(etRealName.getText().toString().trim())){
                TipsToast.showTips(getString(R.string.please_input_your_real_name));
                return;
            }
            if(TextUtils.isEmpty(etDocumentNum.getText().toString().trim())){
                TipsToast.showTips(getString(R.string.please_input_your_document_num));
                return;
            }
            boolean isIDCard=getString(R.string.id_card).equals(fnDocument.getText());
            KycInfo kycInfo=new KycInfo();
            kycInfo.country=CountryCode.getCountryName();
            kycInfo.countryCode=CountryCode.getCountryCode();
            kycInfo.realName=etRealName.getText().toString().trim();
            kycInfo.bornDate=fnBornDate.getText();
            kycInfo.type=isIDCard?"1":"2";
            kycInfo.documentNum=etDocumentNum.getText().toString().trim();
            Bundle bundle=new Bundle();
            bundle.putParcelable(Constant.INTENT_EXTRA_DATA,kycInfo);
            CommonUtil.openActicity(KycActivity.this,isIDCard?IDCardCertifyActivity.class:PassPortCertifyActivity.class,bundle);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //更新国家码
        fnCountry.setText(CountryCode.getCountryName()+CountryCode.getCountryCode());
    }
}

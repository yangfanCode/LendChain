package com.lend.lendchain.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.yangfan.widget.CustomDialog;

/**
 * 自定义控件
 * 手机号 国家代码选择控件
 */
public class CountryCodeView extends LinearLayout {

    private TextView tvCountryCode;
    private View vLine;
    private String countryCode = "+86";//  国家区号
    private CustomDialog customDialogPhone;

    private PhoneTypeListener phoneTypeListener;

    public CountryCodeView(Context context) {
        this(context, null);
    }

    public CountryCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_country_code_view, this, true);
        tvCountryCode = (TextView) findViewById(R.id.tv_countryCode);
        tvCountryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                phone();
            }
        });
        vLine = findViewById(R.id.v_line);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setTvCountryCodeText(String string) {
        tvCountryCode.setText(string);
    }

    public void setData(String countryCode, PhoneTypeListener phoneTypeListener) {
        this.countryCode = countryCode;
        this.phoneTypeListener = phoneTypeListener;
    }

    public TextView getTvCountryCode() {
        return tvCountryCode;
    }

    public View getvLine() {
        return vLine;
    }

    private void phone() {
        // TODO 目前 仅支持 +86
//        if (customDialogPhone == null) {
//            PopUtils popUtils = new PopUtils(getContext());
//            customDialogPhone = popUtils.createPhoneTypeDialog(true, countryCode, new PhoneTypeListener() {
//                @Override
//                public void onPhoneTypeSelect(int position, String countryCode, String regex) {
//                    CountryCodeView.this.countryCode = countryCode;
//                    tvCountryCode.setText(countryCode);
//                    tvCountryCode.setSelected(!tvCountryCode.isSelected());
//                    if (phoneTypeListener != null)
//                        phoneTypeListener.onPhoneTypeSelect(position, countryCode, regex);
//                }
//            });
//
//        } else
//            customDialogPhone.show();
//        tvCountryCode.setSelected(!tvCountryCode.isSelected());
    }

    public interface PhoneTypeListener {
        void onPhoneTypeSelect(int position, String countryCode, String regex);//位置，国家代码，手机号正则
    }

}
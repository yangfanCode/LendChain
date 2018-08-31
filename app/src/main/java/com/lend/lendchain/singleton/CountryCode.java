package com.lend.lendchain.singleton;

import android.text.TextUtils;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 国家码  默认中国+86
 */
public class CountryCode {
    private static String countryCode;
    private static String countryName;
    private CountryCode(){ }
    public static String getCountryCode(){
        if(!TextUtils.isEmpty(countryCode)){
            return countryCode;
        }
        return "+86";
    }
    public static String getCountryName(){
        if(!TextUtils.isEmpty(countryName)){
            return countryName;
        }
        return ContextHelper.getApplication().getString(R.string.china);
    }
    public static void setCountryCode(String countryCode){
        CountryCode.countryCode=countryCode;
    }
    public static void setCountryName(String countryName){
        CountryCode.countryName=countryName;
    }
}

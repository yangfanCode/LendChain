package com.lend.lendchain.singleton;

import android.text.TextUtils;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.utils.LanguageUtils;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 国家码  默认为语言国家
 */
public class CountryCode {
    private static String countryCode;
    private static String countryName;
    private CountryCode(){ }
    public static String getCountryCode(){
        if(!TextUtils.isEmpty(countryCode)){
            return countryCode.replace("+","");
        }
        return getDefaultCode().replace("+","");
    }
    public static String getCountryName(){
        if(!TextUtils.isEmpty(countryName)){
            return countryName;
        }
        return getDefaultCountry();
    }
    public static void setCountryCode(String countryCode){
        CountryCode.countryCode=countryCode;
    }
    public static void setCountryName(String countryName){
        CountryCode.countryName=countryName;
    }

    private static String getDefaultCode(){
        String lan= LanguageUtils.getUserLanguageSetting();
        if(LanguageUtils.SIMPLIFIED_CHINESE.equals(lan)){
            return "86";
        }else if(LanguageUtils.KOREAN.equals(lan)){
            return "82";
        }else{
            return "1";
        }
    }
    private static String getDefaultCountry(){
        String lan= LanguageUtils.getUserLanguageSetting();
        if(LanguageUtils.SIMPLIFIED_CHINESE.equals(lan)){
            return ContextHelper.getApplication().getString(R.string.china);
        }else if(LanguageUtils.KOREAN.equals(lan)){
            return ContextHelper.getApplication().getString(R.string.korea);
        }else{
            return ContextHelper.getApplication().getString(R.string.america);
        }
    }
    public static void clearData(){
        countryCode="";
        countryName="";
    }
}

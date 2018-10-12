package com.lend.lendchain.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.singleton.CountryCode;

import java.util.Locale;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 国际化语言工具类
 * 格式不统一自定义name
 */
public class LanguageUtils {
    public static final String SIMPLIFIED_CHINESE="zh";//简体中文
    public static final String ENGLISH="en";//英文
    public static final String KOREAN="ko";//韩文
    //根据语言设置 返回自定义规则 zh  en ko等 系统语言-->自定义格式
    private static String getCustomLang() {
        if(getLanguageSetting().equals(Locale.SIMPLIFIED_CHINESE.getLanguage())){
            return SIMPLIFIED_CHINESE;
        }else if(getLanguageSetting().equals(Locale.KOREAN.getLanguage())){
            return KOREAN;
        }else{
            return ENGLISH;
        }
    }


    //读取系统语言设置
    private static String getLanguageSetting() {
        return Locale.getDefault().getLanguage().toString();
    }


    //读取存储的选择的语言 读取全局语言使用 主要应用
    public static String getUserLanguageSetting(){
        String lan = SPUtil.getString(SPUtil.KEY_LANGUAGE_SETTING, "");
        if (TextUtils.isEmpty(lan))
            lan = getCustomLang();//为空读取系统语言自定义设置
        return lan;
    }

    //根据Local返回自定义语言 locale-->lan 存
    public static String getCustomLangFromLocal(Locale locale){
        if(locale.getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage())){
            return SIMPLIFIED_CHINESE;
        }else if(locale.getLanguage().equals(Locale.KOREAN.getLanguage())){
            return KOREAN;
        }else{
            return ENGLISH;
        }
    }
    //根据自定义语言返回Local lan-->locale 取
    public static Locale getLocalFromCustomLang(String lan){
        if(lan.equals(SIMPLIFIED_CHINESE)){//简体中文
            return Locale.SIMPLIFIED_CHINESE;
        }else if(lan.equals(KOREAN)){//韩文
            return Locale.KOREAN;
        }else{//英文
            return Locale.ENGLISH;
        }
    }

    /**
     * 对接后台语言参数
     * 规则 中文 zh_CN 英文 en_US 韩文 ko
     * @return
     */
    public static String getLangForHttp(){
        String lan=getUserLanguageSetting();
        if(SIMPLIFIED_CHINESE.equals(lan)){
            return "zh_CN";
        }else if(KOREAN.equals(lan)){
            return "ko";
        }else{
            return "en_US";
        }
    }

    /**
     * 设置语言环境 local 设置
     * 规则： 不是中文简体和韩文就是英文
     */
    public static void saveLanguageSetting(Locale locale) {
        SPUtil.put(SPUtil.KEY_LANGUAGE_SETTING, getCustomLangFromLocal(locale));
        CountryCode.clearData();//默认语言修改
        Resources resources = ContextHelper.getApplication().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            ContextHelper.getApplication().createConfigurationContext(config);
        }
        Locale.setDefault(locale);
        resources.updateConfiguration(config, dm);
    }


}

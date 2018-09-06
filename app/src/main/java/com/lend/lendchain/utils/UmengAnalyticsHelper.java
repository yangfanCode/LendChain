package com.lend.lendchain.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.lend.lendchain.helper.ContextHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * UmengAnalytics
 * Created by yangfan on 2018/3/2.
 */

public class UmengAnalyticsHelper {
    private static final String TAG = "UmengAnalyticsHelper";

    //for key
    public static final String KEY_DEVICE = "device";

    //for normal event
    public static final String SAFE_SUBMIT_SMSCODE = "safe_submit_smsCode";  //提交手机验证码
    public static final String SAFE_SUBMIT_GOOGLECODE = "safe_submit_googleCode";  //提交谷歌验证码
    public static final String SAFE_PHONE_VERIFY = "safe_phone_verify";  //点击“手机认证”
    public static final String SAFE_PHONE_BINDED_SUCCESSFUL = "safe_phone_binded_successful";  //手机绑定成功
    public static final String SAFE_PHONE_BIND = "safe_phone_bind";  //点击“绑定”

    public static final String SAFE_KYC_VERIFY = "safe_kyc_verify";  //点击“身份认证”
    public static final String SAFE_KYC_SUBMIT = "safe_kyc_submit";  //提交“身份认证”
    public static final String SAFE_KYC_PERSONAL_SUBMIT = "safe_kyc_personal_submit";  //提交个人信息
    public static final String SAFE_GOOGLE_VERIFY = "safe_google_verify";  //点击“谷歌认证”
    public static final String SAFE_GOOGLE_BINDED_SUCCESSFUL = "safe_google_binded_successful";  //谷歌绑定成功
    public static final String MINE_SINGUP_SUCCESS = "mine_singUp_success";  //我的注册成功
    public static final String MINE_SINGUP = "mine_singUp";  //点击注册

    public static final String MINE_LOGIN_SUCCESS = "mine_login_success";  //我的登录成功
    public static final String MINE_LOGIN_SIGNUP = "mine_login_signUp";  //点击“注册/登录”
    public static final String MINE_LOGIN = "mine_login";  //点击登录
    public static final String LOAN_SUCCESSFUL = "loan_successful";  //借款成功
    public static final String LOAN_SUBMIT_GOOGLECODE = "loan_submit_googleCode";  //点击提交谷歌验证码
    public static final String LOAN_GOTORECHARGE = "loan_gotoRecharge";  //点击“跳转充值”
    public static final String LOAN_CONFIRM = "loan_confirm";  //点击“确认借款”

    public static final String INVEST_SUCCESSFUL = "invest_successful";  //投资成功
    public static final String INVEST_SUBMIT_GOOGLECODE= "invest_submit_googleCode";  //点击提交谷歌验证码
    public static final String INVEST_GOTORECHARGE = "invest_gotoRecharge";  //点击“跳转充值”
    public static final String INVEST_DETAIL = "invest_detail";  //投资页列表点击
    public static final String INVEST_CONFIRM = "invest_confirm";  //点击“确认投资”
    public static final String INVEST = "invest";  //点击“立即投资”
    public static final String HOME_DETAIL = "home_detail";  //首页推荐立即投资


    public static final String NEWS_NEWSFLASH = "news_newsflash";  //资讯-快讯页面
    public static final String NEWS_HEADLINES = "news_headlines";  //资讯-要闻页面
    public static final String NEWS_TACTIC = "news_tactic";  //资讯-攻略页面
    public static final String NEWS_NEWSFLASH_SHARE = "news_newsflash_share";  //快讯列表分享按钮点击
    public static final String NEWS_HEADLINES_ROTATION = "news_headlines_rotation";  //要闻轮播图点击
    public static final String NEWS_HEADLINES_LIST = "news_headlines_list";  //要闻列表点击
    public static final String NEWS_TACTIC_LIST = "news_tactic_list";  //攻略列表点击

    public static final String EXCHANGE_SEARCH = "exchange_search";  //市场页面搜索按钮点击
    public static final String EXCHANGE_LIST = "exchange_list";  //市场列表点击

    public static final String MINE_EDITOR = "mine_editor";  //点击编辑资料
    public static final String MINE_COLLECT = "mine_collect";  //点击我的收藏
    public static final String MINE_SET= "mine_set";  //点击系统设置

    public static final String MINE_INVITE = "mine_invite";  //点击邀请好友
    public static final String MINE_INVITE_01 = "mine_invite_01";  //点击分享后分享成功
    public static final String MINE_ADVICE = "mine_advice";  //点击意见反馈
    public static final String MINE_ADVICE_01 = "mine_advice_01";  //点击意见反馈后提交成功
    public static final String MINE_ABOUT = "mine_about";  //点击关于我们

    public static final String HOME_WINDOWS_BUTTON_01 = "home_windows_button_01";  //首页弹窗去看看按钮点击UV
    public static final String HOME_WINDOWS_BUTTON_00 = "home_windows_button_00";  //首页弹窗关闭按钮点击UV
    public static final String INCOME_ADD_BUTTON = "income_add_button";  //收益统计添加交易按钮点击PV，UV
    public static final String INCOME_GRAPH_BUTTON = "income_graph_button";  //收益统计曲线按钮点击PV，UV
    public static final String INCOME_ADD_SUCCESD = "income_add_succesd";  //收益统计添加完成按钮PV，UV
    public static final String MINE_INCOME_CARD = "mine_income_card";  //个人中心收益统计卡片点击PV，UV
    public static final String MINE_INCOME_CARD_BEHIND = "mine_income_card_behind";  //个人中心收益统计卡片隐藏资产按钮点击


    public static void umengEvent(String eventId) {
        Map<String, String> map=new HashMap<>();
        map.put("lang",LanguageUtils.getUserLanguageSetting());
        umengEvent(eventId, map);
    }

    public static void umengEvent(String eventId, Map<String, String> map) {
        new WriteTask(eventId, map).execute();
    }


    private static void writeEvent(String eventId, Map<String, String> map) {
        if (TextUtils.isEmpty(eventId))
            return;

        if (null == map)
            MobclickAgent.onEvent(ContextHelper.getApplication(), eventId);
        else
            MobclickAgent.onEvent(ContextHelper.getApplication(), eventId, map);
    }

    /**
     * AsyncTask默认的单线程方式
     */

    private static class WriteTask extends AsyncTask<Void, Void, Void> {
        String eventId;
        Map<String, String> map;

        public WriteTask(String eventId, Map<String, String> map) {
            this.eventId = eventId;
            this.map = map;
        }

        @Override
        protected Void doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();
            writeEvent(eventId, map);
            LogUtils.LogE(UmengAnalyticsHelper.class, "=================umeng onEvent cost = " + (System.currentTimeMillis
                    () - startTime));
            return null;
        }
    }
}


package com.lend.lendchain.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.lend.lendchain.helper.ContextHelper;
import com.umeng.analytics.MobclickAgent;

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
    public static final String OPTIONAL_PAGE = "optional_page";  //自选页面
    public static final String OPTIONAL_LOGIN = "optional_login";  //自选页面登录按钮
    public static final String OPTIONAL_ADD = "optional_add";  //添加自选按钮
    public static final String OPTIONAL_EDITOR = "optional_editor";  //编辑自选按钮
    public static final String OPTIONAL_PAGE_LIST = "optional_page_list";  //自选页面列表

    public static final String MARKET_PAGE = "market_page";  //市值页面
    public static final String MARKET_PAGE_LIST = "optional_page_list";  //市值页面列表
    public static final String MARKET_SORT_NAME = "market_sort_name";  //市值页面按名称排序
    public static final String MARKET_SORT_MARKET = "market_sort_market";  //市值页面按市值排序
    public static final String MARKET_SORT_DEAL = "market_sort_deal";  //市值页面按成交量排序
    public static final String MARKET_SORT_PRICE = "market_sort_price";  //市值页面按最新价排序
    public static final String MARKET_SORT_INCREASE = "market_sort_increase";  //市值页面按24H涨跌幅排序

    public static final String TRANSACTION_PAGE = "transaction_page";  //交易对页面
    public static final String TRANSACTION_PAGE_LIST = "transaction_page_list";  //交易对页面列表
    public static final String TRANSACTION_SORT_NAME = "transaction_sort_name";  //交易对页面按名称排序
    public static final String TRANSACTION_SORT_MARKET = "transaction_sort_market";  //交易对页面按市值排序
    public static final String TRANSACTION_SORT_DEAL = "transaction_sort_deal";  //交易对页面按成交量排序
    public static final String TRANSACTION_SORT_PRICE = "transaction_sort_price";  //交易对页面按最新价排序
    public static final String TRANSACTION_SORT_INCREASE = "transaction_sort_increase";  //交易对页面按24H涨跌幅排序

    public static final String DETAIL_PAGE = "detail_page";  //详情页面
    public static final String DETAIL_ADD= "detail_add";  //详情页面添加自选
    public static final String DETAIL_TABLE_TIME = "detail_table_time";  //详情页面分时按钮
    public static final String DETAIL_TABLE_DAY = "detail_table_day";  //详情页面日线按钮
    public static final String DETAIL_TABLE_WEEK = "detail_table_week";  //详情页面周线按钮
    public static final String DETAIL_TABLE_MONTH = "detail_table_month";  //详情页面月线按钮
    public static final String DETAIL_TABLE_FULLSCREEN = "detail_table_fullscreen";  //详情页面全屏按钮
    public static final String DETAIL_MARKET = "detail_market";  //详情页面市场按钮
    public static final String DETAIL_NEWS = "detail_news";  //详情页面新闻按钮
    public static final String DETAIL_DISCUSSION = "detail_discussion";  //详情页面论币按钮
    public static final String DETAIL_PROSPECTUS = "detail_prospectus";  //详情页面简介按钮
    public static final String DETAIL_NEWS_LIST = "detail_news_list";  //详情页面新闻列表
    public static final String DETAIL_DISCUSSION_SEND = "detail_discussion_send";  //详情页面论币列表发布内容

    public static final String HOME_SEARCH = "home_search";  //行情页面搜索
    public static final String HOME_MESSAGE = "home_message";  //行情页面消息
    public static final String HOME_PAGE = "home_page";  //行情页面

    public static final String NEWS_PAGE = "news_page";  //资讯页面
    public static final String EXCHANGE_PAGE = "exchange_page";  //市场页面
    public static final String MINE_PAGE = "mine_page";  //我的页面

    public static final String NEWS_NEWSFLASH = "news_newsflash";  //资讯-快讯页面
    public static final String NEWS_HEADLINES = "news_headlines";  //资讯-要闻页面
    public static final String NEWS_TACTIC = "news_tactic";  //资讯-攻略页面
    public static final String NEWS_NEWSFLASH_SHARE = "news_newsflash_share";  //快讯列表分享按钮点击
    public static final String NEWS_HEADLINES_ROTATION = "news_headlines_rotation";  //要闻轮播图点击
    public static final String NEWS_HEADLINES_LIST = "news_headlines_list";  //要闻列表点击
    public static final String NEWS_TACTIC_LIST = "news_tactic_list";  //攻略列表点击

    public static final String EXCHANGE_SEARCH = "exchange_search";  //市场页面搜索按钮点击
    public static final String EXCHANGE_LIST = "exchange_list";  //市场列表点击

    public static final String MINE_LOGIN = "mine_login";  //个人中心登录按钮
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
        umengEvent(eventId, null);
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


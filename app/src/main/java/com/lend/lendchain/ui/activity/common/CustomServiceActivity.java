package com.lend.lendchain.ui.activity.common;

import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lend.lendchain.R;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.LoadAnimationUtils;
import com.lend.lendchain.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 客服 页面
 * 返回键 直接返回
 * 不适用webActivity
 */
public class CustomServiceActivity extends BaseActivity {
    @BindView(R.id.web)
    WebView webView;
    private LoadAnimationUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_service);
        StatusBarUtil.setStatusBarColor(CustomServiceActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(CustomServiceActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        utils = new LoadAnimationUtils(this);
        utils.showProcessAnimation();
        webViewSetting();
        webView.loadUrl("http://m.tb.cn/x.enXTpw");
    }

    private void webViewSetting() {
//        syncCookie(this, url);
        WebSettings webSettings = webView.getSettings();
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 如果访问的页面中有Javascript，则webView必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 修改ua使得web端正确判断
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(
                ua.concat(" app_lendchain_Android"));
        // 尝试缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDatabaseEnabled(true);// 开启 数据库存储机制
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        // 设置编码
        webSettings.setDefaultTextEncodingName("utf-8");
//        idwebSettings.setBuiltInZoomControls(true);
        // 设置背景颜色 透明
        webView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        // 设置本地调用对象及其接口
        /*** 打开本地缓存提供JS调用// 开启DOM storage **/
        webSettings.setDomStorageEnabled(true);
        // 设置缓存大小
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        // 支持用户输入获取手势焦点
        webView.requestFocusFromTouch();
        String cacheDirPath = this.getFilesDir().getAbsolutePath() + "cache/";
        webSettings.setDatabasePath(cacheDirPath);// 设置缓存路径
        webSettings.setDatabaseEnabled(true);
        // 只需设置支持JS就自动打开IndexedDB存储机制
        // Android 在4.4开始加入对 IndexedDB 的支持，只需打开允许 JS 执行的开关就好了。

        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
//
//        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webView.addJavascriptInterface(new JsInterface(this), "AndroidwebView");
//        webSettings.setSupportZoom(false);
//        webSettings.setDefaultTextEncodingName("utf-8");// 设置字符编码
        webView.setWebViewClient(webViewClient);// 会导致 js方法失效
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    utils.closeProcessAnimation();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;//true自己处理跳转 刷新url false webView处理跳转
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if(error.getPrimaryError() == android.net.http.SslError.SSL_INVALID ){// 校验过程遇到了bug
                handler.proceed();
            }else{
                handler.cancel();
            }  //接受信任所有网站的证书
        }
    };
}

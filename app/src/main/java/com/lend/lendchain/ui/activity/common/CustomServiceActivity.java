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
        //设置编码
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setUseWideViewPort(true);//适应分辨率
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
//        //支持js
        webSettings.setJavaScriptEnabled(true);
//        //设置背景颜色 透明
        webView.setBackgroundColor(Color.argb(0, 0, 0, 0));

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

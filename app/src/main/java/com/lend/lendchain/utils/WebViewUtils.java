package com.lend.lendchain.utils;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class WebViewUtils {
	
	/**
	 * ie注入Cook
	 * @param context
	 * @param url
	 */
	public static void synCookies(Context context, String url, String... cookies) {
		CookieSyncManager.createInstance(context);
	    CookieManager cookieManager = CookieManager.getInstance();
	    cookieManager.setAcceptCookie(true);
	    int size=cookies.length;
	    for (int i = 0; i < size; i++) {
	    	//cookies是在HttpClient中获得的cookie  
	    	cookieManager.setCookie(url, cookies[i]);
		}
	    CookieSyncManager.getInstance().sync();
	}
	/**
	 * 销毁Cookie
	 * @param context
	 */
	public static void removeCookie(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

}

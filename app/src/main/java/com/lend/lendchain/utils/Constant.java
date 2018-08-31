package com.lend.lendchain.utils;

import android.net.Uri;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class Constant {
    //bungle key
    public static final String INTENT_EXTRA_DATA = "intent_extra_data";
    public static final String ARGS_PARAM1 = "args_param1";
    public static final String ARGS_PARAM2 = "args_param2";
    public static final String ARGS_PARAM3 = "args_param3";
    public static final String ARGS_PARAM4 = "args_param4";
    public static final String ARGS_PARAM5 = "args_param5";
    public static final String ARGS_PARAM6 = "args_param6";
    public static final String ARGS_PARAM7 = "args_param7";

    public static final String LOGIN="login";
    //request code
    public static final int REQUEST_CODE1=100;
    public static final int REQUEST_CODE_WITH_LOGIN=500;
    public static final int RESULT_CODE1=1000;
    public static final int RESULT_CODE2=RESULT_CODE1+1;
    public static final int PAGE_SIZE = 10;
    public static Uri uri;

    //上传图片
    public final static int REQUEST_CODE_PHOTO_CAMERA = 100;
    public final static int REQUEST_CODE_PHOTO_ALBUM = REQUEST_CODE_PHOTO_CAMERA + 1;
    public final static int REQUEST_CODE_PHOTO_CLIP = REQUEST_CODE_PHOTO_ALBUM + 1;


    public static final float PICKER_VIEW_TEXT_SIZE = 20f;//sp
    public static final float PICKER_VIEW_MULTIPLIER = 2.0f;

    //webview
    public static final String INTENT_EXTRA_WEB_IS_REFRESH = "intent_extra_web_is_refresh";// webview 是否可刷新
    public static final String INTENT_EXTRA_TITLE = "intent_extra_title";
    public static final String INTENT_EXTRA_ID = "intent_extra_id";
    public static final String INTENT_EXTRA_URL = "intent_extra_url";
}

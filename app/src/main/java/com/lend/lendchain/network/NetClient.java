package com.lend.lendchain.network;

import android.content.Context;
import android.text.TextUtils;

import com.jkyeo.basicparamsinterceptor.BasicParamsInterceptor;
import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.api.AppApi;
import com.lend.lendchain.network.converter.CustomGsonConverterFactory;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.LoadAnimationUtils;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.widget.TipsToast;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class NetClient {
    public static NetClient mHttpUtils;
    private static AppApi api;

    private static OkHttpClient okHttpClient = null;
    private static LoadAnimationUtils loadAnimationUtils = null;
    private static String mToken = "";
    private static String mCity;

    public static NetClient getInstance() {
        synchronized (NetClient.class) {
            if (null == mHttpUtils) {
                mHttpUtils = new NetClient();
            }
        }
        return mHttpUtils;
    }

    /**
     * 默认请求头方法
     * @param text
     * @param isShow
     * @param activity
     * @return
     */
    public AppApi getPost(String text, boolean isShow, Context activity) {
        if (isShow) {
            if (activity != null) {
                //此处待优化
                loadAnimationUtils = new LoadAnimationUtils(activity);
                loadAnimationUtils.showProcessAnimation(text);
            }
        }
        api = createAppApi(null);// header 中 变量参数太多，不方便判断是否需要重新生成

        return api;
    }

    /**
     * 自定义请求头方法
     * @param text
     * @param isShow
     * @param activity
     * @param headerParamsMap
     * @return
     */
    public AppApi getPost(String text, boolean isShow, Context activity, Map<String, String> headerParamsMap) {
        if (isShow) {
            if (activity != null) {
                loadAnimationUtils = new LoadAnimationUtils(activity);
                loadAnimationUtils.showProcessAnimation(text);
            }
        }
        return createAppApi(headerParamsMap);
    }

    /**
     * 更换主域名方法
     * @param text
     * @param isShow
     * @param activity
     * @param baseUrl
     * @return
     */
    public AppApi getPost(String text, boolean isShow, Context activity, String baseUrl) {
        if (isShow) {
            if (activity != null) {
                loadAnimationUtils = new LoadAnimationUtils(activity);
                loadAnimationUtils.showProcessAnimation(text);
            }
        }
        return createAppApi(null, baseUrl);
    }

    /**
     * 自定义请求头和更换主域名方法
     * @param text
     * @param isShow
     * @param activity
     * @param headerParamsMap
     * @param baseUrl
     * @return
     */
    public AppApi getPost(String text, boolean isShow, Context activity, Map<String, String> headerParamsMap, String baseUrl) {
        if (isShow) {
            if (activity != null) {
                loadAnimationUtils = new LoadAnimationUtils(activity);
                loadAnimationUtils.showProcessAnimation(text);
            }
        }
        return createAppApi(headerParamsMap, baseUrl);
    }


    private AppApi createAppApi(Map<String, String> headerParamsMap) {
        return createAppApi(headerParamsMap, null);
    }

    private AppApi createAppApi(Map<String, String> headerParamsMap, String baseUrl) {
        BasicParamsInterceptor basicParamsInterceptor =
                new BasicParamsInterceptor.Builder()
                        .addHeaderParamsMap(headerParamsMap != null ? headerParamsMap : CommonUtil.getHeaderParamsMap())
                        .addParam("lang", LanguageUtils.getLangForHttp())//固定语言参数
                        .build();

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(basicParamsInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(TextUtils.isEmpty(baseUrl) ? NetConst.dynamicBaseUrl() : baseUrl)
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(AppApi.class);
    }

    public static abstract class RxObserver<T> implements Observer<T> {

        //        @Override
//        public void onStart() {
//            super.onStart();
//            LogUtil.LogE(HttpUtils.class, "onStart");
//        }
        private String apiUrl;
        private Object obj;
        public RxObserver(){}
        public RxObserver(String apiUrl){
            this.apiUrl=apiUrl;
        }

        public RxObserver(String apiUrl, Object obj) {
            this.apiUrl = apiUrl;
            this.obj = obj;
        }

        public Object getObj() {
            return obj;
        }

        //public RxObserver(){}
        @Override
        public void onCompleted() {
            if (loadAnimationUtils != null) {
                loadAnimationUtils.closeProcessAnimation();
                loadAnimationUtils = null;
            }
        }

        @Override
        public void onError(Throwable e) {
            if (loadAnimationUtils != null) {
                loadAnimationUtils.closeProcessAnimation();
                loadAnimationUtils = null;
            }
            LogUtils.LogE(NetClient.class, "error-->   " + e.getMessage());
            e.printStackTrace();
            //在这里做全局的错误处理
            if (e instanceof HttpException) {
//                ToastTool.showToast(MyApplicaion.getContext(), "HttpException");
            } else if (e instanceof UnknownHostException) {// 网络未连接
                TipsToast.showTips(ContextHelper.getApplication().getString(R.string.netWorkError));
            }
        }

        @Override
        public void onNext(T t) {
            LogUtils.LogE(NetClient.class, "next");
//            if(t instanceof ResultBean){
//                ResultBean resultBean= (ResultBean) t;
//                if (resultBean != null) {
//                if("2002".equals(resultBean.code)){//登录失效
//                    //登录失效立即登录 后面补充逻辑
//                    CommonUtil.goToLogin(ContextHelper.getApplication());
//                }else{
////
////                }
////                if(resultBean.code!=0){//code不为0上传错误日志
////                    LogUtils.LogE(NetClient.class,"saveErrorLog:"+resultBean.code+" "+apiUrl);
////                    if(!TextUtils.isEmpty(apiUrl)){
////                        NetApi.saveErrorLog(ContextHelper.getApplication(), apiUrl, resultBean.resultMsg, new RxObserver<T>("") {
////                            @Override
////                            public void onSuccess(T t) {
////
////                            }
////                        });
////                    }
//                }
//                }
//            }
            onSuccess(t);
        }

        public abstract void onSuccess(T t);


    }

}

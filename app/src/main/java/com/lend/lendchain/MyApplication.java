package com.lend.lendchain;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.lend.lendchain.enums.AppEnvEnum;
import com.lend.lendchain.enums.AppEnvHelper;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.network.subscriber.SafeOnlyNextSubscriber;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.FrescoUtils;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.LogUtils;
import com.lend.lendchain.utils.SPUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import rx.Subscription;
import rx.observers.SafeSubscriber;
import rx.plugins.RxJavaObservableExecutionHook;
import rx.plugins.RxJavaPlugins;


/**
 * Created by js on 2016/11/29.
 */
public class MyApplication extends Application {

	private Handler mHandler = new Handler();
	//static 代码段可以防止内存泄露
	static {
		//设置全局的Header构建器
		SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColors(ColorUtils.WHITE);//全局设置主题颜色
			//修改文案 国际化
			ClassicsHeader.REFRESH_HEADER_PULLING=context.getString(R.string.refrensh_header_pulling);
			ClassicsHeader.REFRESH_HEADER_FINISH=context.getString(R.string.refrensh_header_finish);
			ClassicsHeader.REFRESH_HEADER_REFRESHING=context.getString(R.string.refrensh_header_refreshing);
			ClassicsHeader.REFRESH_HEADER_RELEASE=context.getString(R.string.refrensh_header_release);
			ClassicsHeader.REFRESH_HEADER_UPDATE=context.getString(R.string.refrensh_header_update);
            return new ClassicsHeader(context).setAccentColor(ColorUtils.COLOR_595959).setFinishDuration(0).setTextTimeMarginTop(3f);
        });
		//设置全局的Footer构建器
		SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
			//修改文案 国际化
			ClassicsFooter.REFRESH_FOOTER_LOADING=context.getString(R.string.refrensh_footer_loading);
			ClassicsFooter.REFRESH_FOOTER_NOTHING=context.getString(R.string.refrensh_footer_nothing);
            return new ClassicsFooter(context).setFinishDuration(0).setAccentColor(ColorUtils.COLOR_595959);
        });
	}
	@Override
	public void onCreate() {
		super.onCreate();
		MultiDex.install(this);
		ContextHelper.initWithApplication(this);
		SPUtil.initWithApplication(this);
//		Fresco.initialize(this);
		FrescoUtils.initialize(this);
		LogUtils.setAppEnvEnum(AppEnvHelper.currentEnv());
		initLangeuage();//初始化语言
		RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaObservableExecutionHook() {//hook rxjava observer
			@Override
			public Subscription onSubscribeReturn(Subscription subscription) {
				if (subscription instanceof SafeSubscriber) {
					SafeSubscriber safeSubscriber = (SafeSubscriber) subscription;
					Subscription sub = safeSubscriber.getActual();
					BaseActivity act = ContextHelper.getLastActivity();
					if (act != null && (sub instanceof SafeOnlyNextSubscriber)) {//如果是耗时请求的话
						act.addSubscription(sub);
					}
				}
				return super.onSubscribeReturn(subscription);
			}
		});
		try {//初始化阿里投篮验证
			SecurityInit.Initialize(this);
		} catch (JAQException e) {
			e.printStackTrace();
		}
	}

	private void initLangeuage() {
		String lan = LanguageUtils.getUserLanguageSetting();//读取语言设置
		LogUtils.LogD(MyApplication.class, "================之前选择的语言 : " + lan);
		LanguageUtils.saveLanguageSetting(LanguageUtils.getLocalFromCustomLang(lan));
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	/**
	 * 在主线程执行方法
	 **/
	public void runOnUiThread(Runnable runnable) {
		if (runnable != null) mHandler.post(runnable);
	}

	/**
	 * 延时执行
	 **/
	public void runDelay(Runnable runnable, long delay) {
		if (runnable != null) mHandler.postDelayed(runnable, delay);
	}

//	{
//		PlatformConfig.setWeixin("wxe429888b5b3f1797", "a848751ded09fbc94924cec7b38e454a");
//		PlatformConfig.setQQZone("101378577", "6dcf18afe6acec35382d7f0e9a621ac2");
//	}

	private boolean isDebug(){
		if (AppEnvHelper.currentEnv() == AppEnvEnum.DEBUG) {
			return true;
		}
		return false;
	}

}

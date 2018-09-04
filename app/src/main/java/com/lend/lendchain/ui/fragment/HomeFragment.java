package com.lend.lendchain.ui.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.adapter.HomeMarketAdapter;
import com.lend.lendchain.bean.Banner;
import com.lend.lendchain.bean.HomeMarket;
import com.lend.lendchain.bean.HomeMarketKLine;
import com.lend.lendchain.bean.HomeSupport;
import com.lend.lendchain.bean.MessageEvent;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.RxBus;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.network.subscriber.SafeOnlyNextSubscriber;
import com.lend.lendchain.ui.activity.common.WebActivity;
import com.lend.lendchain.ui.activity.invest.InvestSummaryActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.LanguageUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.CircleProgressBar;
import com.lend.lendchain.widget.GradientTextView;
import com.lend.lendchain.widget.MyListView;
import com.lend.lendchain.widget.ScrollChangeView;
import com.lend.lendchain.widget.TipsToast;
import com.lend.lendchain.widget.ZoomOutPageTransformer;
import com.lend.lendchain.widget.chart.view.HomeMarketFenshiView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.yangfan.widget.CustomBannerView;
import com.yangfan.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 首页fragment 沉浸式状态栏
 */
public class HomeFragment extends Fragment {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.bannerView)
    CustomBannerView bannerView;
    @BindView(R.id.home_content_vp)
    CustomViewPager customViewPager;
    @BindView(R.id.scrollView)
    ScrollChangeView scrollView;
    @BindView(R.id.home_llController)
    LinearLayout llController;
    @BindView(R.id.home_quetes_lv)
    MyListView lvQuetes;
    private View parentView;
    private HomeMarketFenshiView fenshiView;//kLine
    private HomeMarketAdapter adapter;
    private double rateRMBDollar;
    private Subscription rxSubscription;
    private List<Banner.Detail> bannerData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_home, container,
                    false);
            initView();
            initData(true);
            initListener();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);

        return parentView;
    }


    private void initView() {
        ButterKnife.bind(this, parentView);
        refreshLayout.setEnableLoadMore(false);
        adapter = new HomeMarketAdapter(getActivity());
        lvQuetes.setAdapter(adapter);
        bannerData=new ArrayList<>();
        getRxBus();
    }

    private void initData(boolean isShow) {
        //banner 接口
        NetApi.banner(getActivity(), bannerObserver);
        adapter.setShowPos(-1);//默认行情不展开
        //contentviewpager 首页推荐投资接口
        NetApi.homeSupport(getActivity(), homeSupportObserver);
        //首页行情市值汇率接口 链式请求 行情市值接口
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", getString(R.string.symbol));//跟据语言不通 更换 symol值 美元请求中文的成功后汇率1
        NetClient.getInstance().getPost("", isShow, getActivity()).coinTransferRate(map)
                .subscribeOn(Schedulers.io())
                .flatMap(resultBean -> {
                    if (resultBean.isSuccess()) {
                        if("en_US".equals(LanguageUtils.getLangForHttp())){
                            rateRMBDollar = 1;//汇率 //美元特殊处理
                        }else{
                            rateRMBDollar = resultBean.data.price;//汇率
                        }
                    }
                    return NetClient.getInstance().getPost("", false, getActivity()).homeMarket();
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetClient.RxObserver<ResultBean<List<HomeMarket>>>() {
                    @Override
                    public void onSuccess(ResultBean<List<HomeMarket>> listResultBean) {
                        if (listResultBean == null) return;
                        if (listResultBean.isSuccess()) {
                            if (listResultBean.data != null) {
                                adapter.loadData(listResultBean.data, rateRMBDollar);
                            }
                        } else {
                            TipsToast.showTips(listResultBean.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        TipsToast.showTips(getString());
                    }
                });
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            initData(false);
            refreshlayout.finishRefresh(300);
        });
        //监听滑动改变状态栏颜色
        refreshLayout.setOnMultiPurposeListener(new OnMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                if(offset==0){
                    StatusBarUtil.StatusBarDarkMode(getActivity());
                }else{
                    StatusBarUtil.StatusBarLightMode(getActivity());
                }
            }
            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) { }
            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) { }
            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) { }
            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) { }
            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) { }
            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) { }
            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) { }
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) { }
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) { }
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) { }
        });
        //根据滑动距离监听pulltorefrenshScrollview  进行状态栏颜色变化 下拉刷新时
        scrollView.setScrollViewListener((scrollView, x, y, oldx, oldy) -> {
            if (y > DisplayUtil.dp2px(getActivity(), 210)) {
                StatusBarUtil.StatusBarLightMode(getActivity());
            } else {
                StatusBarUtil.StatusBarDarkMode(getActivity());
            }
        });
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {//改变圆点控制器
                for (int i = 0; i < llController.getChildCount(); i++) {
                    boolean isSelect = i == position;//是否选择
                    ImageView imageView = (ImageView) llController.getChildAt(i);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    params.width = isSelect ? DisplayUtil.dp2px(getActivity(), 15) : DisplayUtil.dp2px(getActivity(), 6);
                    params.height = DisplayUtil.dp2px(getActivity(), 6);
                    imageView.setLayoutParams(params);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), i == position ? R.mipmap.icon_controller_p : R.mipmap.icon_controller));
//                    imageView.setImageResource(i == position ? R.mipmap.icon_controller_p : R.mipmap.icon_controller);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bannerView.setOnBannerItemClickListener(i -> {//banner点击
            //不需要登录 或者需要登录并且已经登陆的 否则跳转登录
            boolean isNeedLogin=bannerData.get(i).isNeedLogin();
            if(!isNeedLogin||(isNeedLogin&&CommonUtil.isLoginElseGotoLogin(getActivity()))){
                Bundle bundle=new Bundle();
                bundle.putString(Constant.INTENT_EXTRA_URL,bannerData.get(i).href);
                bundle.putString(Constant.INTENT_EXTRA_TITLE,bannerData.get(i).title);
                CommonUtil.openActicity(getActivity(), WebActivity.class,bundle);
            }
        });
    }

    private void getRxBus() {
        rxSubscription = RxBus.getInstance().toObserverable(MessageEvent.class).subscribe(new SafeOnlyNextSubscriber<MessageEvent>() {
            @Override
            public void onNext(MessageEvent args) {
                super.onNext(args);
                int type = args.type;
                if (type == MessageEvent.HOME_MARKEY_KLINE) {//点击展示Klineline
                    int pairId = args.position;
                    fenshiView = (HomeMarketFenshiView) args.data;
                    NetApi.KLinePrice24h(getActivity(), pairId, kLineObserver);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getRxBus();//rxbus报错后 会自动取消订阅 在error重新订阅
            }
        });
    }

    //banner
    Observer<Banner> bannerObserver=new NetClient.RxObserver<Banner>() {
        @Override
        public void onSuccess(Banner banner) {
            if(banner==null)return;
            bannerData.clear();
            if(LanguageUtils.SIMPLIFIED_CHINESE.equals(LanguageUtils.getUserLanguageSetting())){//简体中文
                bannerData.addAll(banner.data.cn);
            }else if(LanguageUtils.KOREAN.equals(LanguageUtils.getUserLanguageSetting())){//韩文
                bannerData.addAll(banner.data.ko);
            }else{//英文
                bannerData.addAll(banner.data.en);
            }
            List<String> img_data = new ArrayList<>();
            for (int i = 0, size = bannerData.size(); i < size; i++) {
                img_data.add(bannerData.get(i).url);
            }
            bannerView.setViewUrls(img_data);
            bannerView.startAutoPlay();//开启轮播
        }
    };
    //首页推荐
    Observer<ResultBean<List<HomeSupport>>> homeSupportObserver = new NetClient.RxObserver<ResultBean<List<HomeSupport>>>() {
        @Override
        public void onSuccess(ResultBean<List<HomeSupport>> listResultBean) {
            if (listResultBean == null) return;
            if (listResultBean.isSuccess()) {
                if (listResultBean.data != null) {
                    initHomeSupportData(listResultBean);
                }
            } else {
                TipsToast.showTips(listResultBean.message);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean<List<HomeMarketKLine>>> kLineObserver = new NetClient.RxObserver<ResultBean<List<HomeMarketKLine>>>() {
        @Override
        public void onSuccess(ResultBean<List<HomeMarketKLine>> listResultBean) {
            if (listResultBean == null) return;
            if (listResultBean.isSuccess()) {
                if (listResultBean.data != null) {
                    ArrayList<HomeMarketKLine> kLineArrayList = new ArrayList<>();
                    kLineArrayList.addAll(listResultBean.data);
                    Collections.reverse(kLineArrayList);
                    fenshiView.setDataAndInvalidate(kLineArrayList);
                    adapter.notifyDataSetChanged();
                }
            } else {
                TipsToast.showTips(listResultBean.message);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rxSubscription.isUnsubscribed()) {
            getRxBus();
        }
    }

    /**
     * 处理首页推荐数据
     *
     * @param listResultBean
     */
    private void initHomeSupportData(ResultBean<List<HomeSupport>> listResultBean) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        List<View> listViews = new ArrayList<>();
        for (int i = 0; i < listResultBean.data.size(); i++) {//处理数据
            HomeSupport homeSupport = listResultBean.data.get(i);
            View v = inflater.inflate(R.layout.item_home_support_viewpager, null);
            TextView tvOrderId = v.findViewById(R.id.item_home_tvOrderId);//名称
            TextView tvOrderType = v.findViewById(R.id.item_home_tvOrderType);//类型
            TextView tvOrderDays = v.findViewById(R.id.item_home_tvOrderDays);//时间
            TextView tvBorrowAmount = v.findViewById(R.id.item_home_tvBorrowAmount);//借入资产
            TextView tvMinInvestAmount = v.findViewById(R.id.item_home_tvMinInvestAmount);//最小投资额
            CircleProgressBar progressBar = v.findViewById(R.id.item_home_progressBar);//进度条
            GradientTextView tvAnnualized = v.findViewById(R.id.item_home_tvAnnualized);//年化
            TextView btnInvest=v.findViewById(R.id.item_home_btnInvest);//立即投资
            tvOrderId.setText(homeSupport.orderId);
            tvOrderType.setText(getOrderType(homeSupport.borrowTypeId));
            tvOrderDays.setText(homeSupport.borrowDays + getString(R.string.day_period));
            progressBar.setProgress((int) (homeSupport.boughtAmount / homeSupport.borrowAmount * 100));//进度条
            tvAnnualized.setText(DoubleUtils.doubleRoundFormat(homeSupport.interestRates * 360 * 100, 2));//2位小数
            tvBorrowAmount.setText(DoubleUtils.doubleTransRoundTwo(homeSupport.borrowAmount, 2).concat(" "+homeSupport.borrowCryptoCode));//单位不处理
            tvMinInvestAmount.setText(DoubleUtils.doubleTransRoundTwo(homeSupport.minInvestAmount, 2).concat(" "+homeSupport.borrowCryptoCode));//单位不处理
            btnInvest.setOnClickListener(v1 -> {
                Bundle bundle=new Bundle();
                //传标的id
                bundle.putString(Constant.INTENT_EXTRA_DATA,homeSupport.id);
                bundle.putInt(Constant.ARGS_PARAM1,Integer.valueOf(homeSupport.borrowTypeId));
                CommonUtil.openActivityWithLogin(getActivity(), InvestSummaryActivity.class,bundle);
            });
            listViews.add(v);
        }
        int size = listViews.size();
        ContentPagerAdapter adapter = new ContentPagerAdapter(listViews);
//                    customViewPager.setPageMargin(DisplayUtil.dp2px(getActivity(),5));//设置间距 设置zoom不需要设置margain
        customViewPager.setOffscreenPageLimit(size);
        customViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        customViewPager.setAdapter(adapter);
        customViewPager.setCurrentItem(1);//展示第2条
        initController(size, 1);//圆点指示器
    }

    /**
     * 获得类型
     *
     * @param borrowTypeId
     * @return
     */
    private String getOrderType(String borrowTypeId) {
        if ("1".equals(borrowTypeId)) {
            return "("+getString(R.string.mortgage)+")";
        } else if ("2".equals(borrowTypeId)) {
            return "("+getString(R.string.preview)+")";
        } else if ("3".equals(borrowTypeId)) {
            return "("+getString(R.string.platForm)+")";
        } else if ("4".equals(borrowTypeId)) {
            return "("+getString(R.string.novice_preview)+")";
        } else if ("5".equals(borrowTypeId)) {
            return "("+getString(R.string.novice)+")";
        } else {
            return "";
        }
    }

    /**
     * @param size       总个数
     * @param defaultPos 默认选择
     */
    private void initController(int size, int defaultPos) {
        llController.removeAllViews();
        ;
        for (int i = 0; i < size; i++) {
            boolean isSelect = i == defaultPos;//是否选择
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = isSelect ? DisplayUtil.dp2px(getActivity(), 15) : DisplayUtil.dp2px(getActivity(), 6);
            params.height = DisplayUtil.dp2px(getActivity(), 6);
            params.setMargins(0, 0, DisplayUtil.dp2px(getActivity(), 3), 0);//边距
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), i == defaultPos ? R.mipmap.icon_controller_p : R.mipmap.icon_controller));
//            imageView.setImageResource(i == defaultPos ? R.mipmap.icon_controller_p : R.mipmap.icon_controller);//设置图片
            llController.addView(imageView);
        }
    }

    //首页投资 内容viewPager适配器
    class ContentPagerAdapter extends PagerAdapter {

        private List<View> list;

        public ContentPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}

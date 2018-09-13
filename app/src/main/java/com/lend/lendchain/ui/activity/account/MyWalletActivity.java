package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyWalletList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.MyWalletAdapter;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyWalletActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.my_wallet_lv)
    ListViewWithOptional lv;
    private int currentPage = 1;
    private boolean isRefrensh = false;
    private MyWalletAdapter adapter;
    private TextView tvHeadTotalMoney,tvYue;
    private String myWalletTotalMoney;//总金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fitSystemWindows = false;//设置布局顶到状态栏
        setContentView(R.layout.activity_my_wallet);
        StatusBarUtil.StatusBarDarkMode(MyWalletActivity.this);//状态栏图标白色
        int height = CommonUtil.getStatusBarHeight();//获取状态栏高度 设置padding
        baseTitleBar.setPadding(0, height, 0, 0);//设置是状态栏渐变色
        //设置 我的钱包页面 沉浸式刷新
        refreshLayout.setRefreshHeader(new ClassicsHeader(this).setAccentColor(ColorUtils.WHITE).setFinishDuration(0).setTextTimeMarginTop(3f));
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.getTvTitle().setTextColor(ColorUtils.WHITE);
        baseTitleBar.setTitle(getString(R.string.my_wallet));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        refreshLayout.setEnableLoadMore(false);
        adapter = new MyWalletAdapter(MyWalletActivity.this);
        lv.setAdapter(adapter);
        View head= LayoutInflater.from(this).inflate(R.layout.head_my_wallet_lv,null);
        tvHeadTotalMoney = (TextView)head. findViewById(R.id.head_my_wallet_tvTotalMoney);
        tvYue = (TextView)head. findViewById(R.id.head_my_wallet_tvYue);
        if(lv.getHeaderViewsCount()==0)lv.addHeaderView(head);
        initData(true);
        initstener();
    }


    private void initData(boolean isShow) {
        currentPage = 1;
        isRefrensh = true;
        //头部列表链式请求
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", SPUtil.getToken());
        NetClient.getInstance().getPost("", isShow, MyWalletActivity.this).getUserInfo(map).subscribeOn(Schedulers.io())
                .flatMap(userInfoResultBean -> {
                    myWalletTotalMoney= DoubleUtils.doubleTransRound6(userInfoResultBean.data.totalAmount);//总余额
                    Map<String, Object> mapWalletList = new HashMap<>();
                    mapWalletList.put("access_token", SPUtil.getToken());
                    mapWalletList.put("page", currentPage);
                    mapWalletList.put("page_size", Constant.PAGE_SIZE);
                    return NetClient.getInstance().getPost("", false, MyWalletActivity.this).myWalletList(mapWalletList);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myWalletListObserver);
    }

    private void reloadData() {
        isRefrensh = false;
        NetApi.myWalletList(MyWalletActivity.this, false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, myWalletListObserver);
    }

    private void initstener() {
//        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                initData(false);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                reloadData();
//            }
//        });
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
    }

    Observer<ResultBean<List<MyWalletList>>> myWalletListObserver = new NetClient.RxObserver<ResultBean<List<MyWalletList>>>() {
        @Override
        public void onSuccess(ResultBean<List<MyWalletList>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    tvHeadTotalMoney.setText(myWalletTotalMoney);
                    tvYue.setVisibility(View.VISIBLE);
                    if (isRefrensh)
                        adapter.loadData(resultBean.data);
                    else
                        adapter.reLoadData(resultBean.data);

                }

            } else {
                TipsToast.showTips(resultBean.message);
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            refreshLayout.finishRefresh();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
            refreshLayout.finishRefresh();

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        initData(true);//返回刷新数据
    }
}

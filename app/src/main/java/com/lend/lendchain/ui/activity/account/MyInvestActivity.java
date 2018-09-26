package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyInvestList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.MyInvestListAdapter;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.utils.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class MyInvestActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.my_invest_lv)
    ListViewWithOptional lv;
    private MyInvestListAdapter adapter;
    private int currentPage = 1;
    private boolean isRefrensh=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invest);
        StatusBarUtil.setStatusBarColor(MyInvestActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(MyInvestActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.my_invest));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        adapter = new MyInvestListAdapter(MyInvestActivity.this);
        lv.setAdapter(adapter);
        initData(true);
        initListener();
    }

    private void initData(boolean isShow) {
        isRefrensh=true;
        currentPage=1;
        NetApi.myInvestList(MyInvestActivity.this, isShow, SPUtil.getToken(), currentPage, Constant.PAGE_SIZE,investListObserver );
    }
    private void reLoadData() {
        isRefrensh=false;
        NetApi.myInvestList(MyInvestActivity.this, false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, investListObserver);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
        refreshLayout.setOnLoadMoreListener(refreshlayout -> reLoadData());
    }

    Observer<ResultBean<List<MyInvestList>>> investListObserver = new NetClient.RxObserver<ResultBean<List<MyInvestList>>>() {
        @Override
        public void onSuccess(ResultBean<List<MyInvestList>> listResultBean) {
            if (listResultBean == null) return;
            if (listResultBean.isSuccess()) {
                if(listResultBean.data!=null&&listResultBean.data.size()>0){
                    if(isRefrensh)
                        adapter.loadData(listResultBean.data);
                    else
                        adapter.reloadData(listResultBean.data);
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            } else {
                TipsToast.showTips(listResultBean.message);
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            finishRefrensh();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            finishRefrensh();
        }
    };
    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }
}

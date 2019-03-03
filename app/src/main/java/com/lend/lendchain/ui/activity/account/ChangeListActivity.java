package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ChangeList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.ChangeListAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class ChangeListActivity extends BaseActivity {
    @BindView(R.id.coin_change_lv)
    ListViewWithOptional lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int currentPage=1;
    private ChangeListAdapter adapter;
    private boolean isRefrensh=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_list);
        StatusBarUtil.setStatusBarColor(ChangeListActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(ChangeListActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.change_list));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        adapter=new ChangeListAdapter(ChangeListActivity.this);
        lv.setAdapter(adapter);
        initData();
        initListener();
    }


    private void initData() {
        isRefrensh=true;
        currentPage = 1;
        //获取兑换列表
        NetApi.changeList(this,false,SPUtil.getToken(),currentPage, Constant.PAGE_SIZE,changeListObserver);
    }

    private void reLoadData(){
        isRefrensh=false;
        //获取兑换列表
        NetApi.changeList(this,false,SPUtil.getToken(),++currentPage,Constant.PAGE_SIZE,changeListObserver);
    }


    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshLayout ->initData());
        refreshLayout.setOnLoadMoreListener(refreshLayout -> reLoadData());
    }


    Observer<ResultBean<ChangeList>> changeListObserver=new NetClient.RxObserver<ResultBean<ChangeList>>() {
        @Override
        public void onSuccess(ResultBean<ChangeList> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null&&resultBean.data.records.size()>0){
                    if(isRefrensh)
                        adapter.loadData(resultBean.data.records);
                    else
                        adapter.reLoadData(resultBean.data.records);
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }else{
                setHttpFailed(ChangeListActivity.this, resultBean);
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
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }

}

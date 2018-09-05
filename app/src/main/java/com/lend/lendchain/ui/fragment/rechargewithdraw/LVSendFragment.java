package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SendLvRecord;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.fragment.rechargewithdraw.adapter.SendLvAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class LVSendFragment extends Fragment {
    @BindView(R.id.lvSendr_record_lv)
    ListViewWithOptional lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View parentView;
    private int currentPage = 1;
    private boolean isRefrensh = true;
    private SendLvAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_lvsend, container,
                    false);
            initView();
            initData(false);
            initListener();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    public static LVSendFragment newInstance(){
        LVSendFragment fragment = new LVSendFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }
    private void initView() {
        ButterKnife.bind(this,parentView);
//        lv.setMode(PullToRefreshBase.Mode.BOTH);
        adapter=new SendLvAdapter(getActivity());
        lv.setAdapter(adapter);
    }

    private void initData(boolean isShow) {
        isRefrensh = true;
        currentPage = 1;
        NetApi.sendLvRecord(getActivity(), isShow, SPUtil.getToken(),currentPage, Constant.PAGE_SIZE,sendLvRecordObserver );
    }

    private void reLoadData() {
        isRefrensh = false;
        NetApi.sendLvRecord(getActivity(), false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, sendLvRecordObserver);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            initData(false);
        });
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                reLoadData();
            }
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData(false);
            }
        });
    }

    Observer<ResultBean<SendLvRecord>> sendLvRecordObserver = new NetClient.RxObserver<ResultBean<SendLvRecord>>() {
        @Override
        public void onSuccess(ResultBean<SendLvRecord> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
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
            } else {
                TipsToast.showTips(resultBean.message);
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

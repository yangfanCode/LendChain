package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.RechargeWithDraw;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.adapter.WithDrawRecordAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithDrawRecordFragment extends Fragment {

    @BindView(R.id.withdraw_record_lv)
    ListViewWithOptional lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View parentView;
    private int currentPage = 1;
    private boolean isRefrensh = true;
    private WithDrawRecordAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_with_draw, container,
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
    public static WithDrawRecordFragment newInstance(){
        WithDrawRecordFragment fragment = new WithDrawRecordFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        ButterKnife.bind(this,parentView);
        //初始化SmartRefrenshLayout属性
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutCommon(refreshLayout);
        adapter=new WithDrawRecordAdapter(getActivity());
        lv.setAdapter(adapter);
    }

    private void initData(boolean isShow) {
        isRefrensh = true;
        currentPage = 1;
        NetApi.myWithDrawList(getActivity(), isShow, SPUtil.getToken(),currentPage, Constant.PAGE_SIZE,rechargeRecordObserver );
    }

    private void reLoadData() {
        isRefrensh = false;
        NetApi.myWithDrawList(getActivity(), false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, rechargeRecordObserver);
    }

    private void initListener() {
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

    Observer<ResultBean<List<RechargeWithDraw>>> rechargeRecordObserver = new NetClient.RxObserver<ResultBean<List<RechargeWithDraw>>>() {
        @Override
        public void onSuccess(ResultBean<List<RechargeWithDraw>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if(resultBean.data!=null&&resultBean.data.size()>0){
                    if(isRefrensh)
                        adapter.loadData(resultBean.data);
                    else
                        adapter.reLoadData(resultBean.data);
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            } else {
                ((BaseActivity)getActivity()).setHttpFailed(getActivity(),resultBean);
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

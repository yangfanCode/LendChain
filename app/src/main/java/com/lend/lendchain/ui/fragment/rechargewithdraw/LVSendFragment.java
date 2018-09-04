package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.RechargeWithDraw;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.fragment.rechargewithdraw.adapter.TransferRecordAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class LVSendFragment extends Fragment {
    @BindView(R.id.lvSendr_record_lv)
    ListViewWithOptional lv;
    private View parentView;
    private int currentPage = 1;
    private boolean isRefrensh = true;
    private TransferRecordAdapter adapter;

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
        adapter=new TransferRecordAdapter(getActivity());
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
//        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                initData(false);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                reLoadData();
//            }
//        });
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
                    lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                }
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }
        @Override
        public void onCompleted() {
            super.onCompleted();
//            lv.onRefreshComplete();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
//            lv.onRefreshComplete();
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };
}

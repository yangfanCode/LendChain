package com.lend.lendchain.ui.fragment.invest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.InvestList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.invest.InvestSummaryActivity;
import com.lend.lendchain.ui.fragment.invest.adapter.InvestAllAdapter;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.UmengAnalyticsHelper;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 投资平台标fragment
 */
public class InvestPlatFormFragment extends Fragment {

    @BindView(R.id.invest_listView)
    ListViewWithOptional listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int currentPage=1;
    private View parentView;
    private InvestAllAdapter investAllAdapter;
    private boolean isRefrensh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_invest_plat_form, container,
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

    public static InvestPlatFormFragment newInstance(){
        InvestPlatFormFragment fragment = new InvestPlatFormFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        ButterKnife.bind(this,parentView);
        investAllAdapter=new InvestAllAdapter(getActivity());
        listView.setAdapter(investAllAdapter);
    }

    private void initData(boolean isShow) {
        currentPage=1;
        isRefrensh=true;
        NetApi.investList(getActivity(),isShow,currentPage, Constant.PAGE_SIZE,1,observer);
    }
    private void loadData() {
        isRefrensh=false;
        NetApi.investList(getActivity(),false,++currentPage, Constant.PAGE_SIZE,1,observer);
    }


    private void initListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            //友盟埋点 列表点击
            UmengAnalyticsHelper.umengEvent(UmengAnalyticsHelper.INVEST_DETAIL);
            Bundle bundle=new Bundle();
            //传标的id
            bundle.putString(Constant.INTENT_EXTRA_DATA,investAllAdapter.getItem(position).id);
            CommonUtil.openActicity(getActivity(), InvestSummaryActivity.class,bundle);
        });
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
        refreshLayout.setOnLoadMoreListener(refreshlayout -> loadData());
    }


    Observer<ResultBean<List<InvestList>>> observer=new NetClient.RxObserver<ResultBean<List<InvestList>>>() {
        @Override
        public void onSuccess(ResultBean<List<InvestList>> listResultBean) {
            if(listResultBean==null)return;
            if(listResultBean.isSuccess()){
                if(listResultBean.data!=null&&listResultBean.data.size()>0){
                    if(isRefrensh){
                        investAllAdapter.loadData(listResultBean.data);
                    }else{
                        investAllAdapter.reLoadData(listResultBean.data);
                    }
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        listView.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }else{
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InvestPlatFormFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InvestPlatFormFragment");
    }

    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }
}

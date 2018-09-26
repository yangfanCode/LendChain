package com.lend.lendchain.ui.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyLoanList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.callback.MyInterface;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.MyLoanListAdapter;
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

public class MyLoanActivity extends BaseActivity implements MyInterface.notifyPos{
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.my_loan_lv)
    ListViewWithOptional lv;
    private int currentPage = 1;
    private boolean isRefrensh=true;
    private MyLoanListAdapter adapter;
    private String status;//状态
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loan);
        StatusBarUtil.setStatusBarColor(MyLoanActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(MyLoanActivity.this);
    }
    //详情页返回
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getExtras()!=null){
            //局部刷新
            int position=intent.getExtras().getInt(Constant.INTENT_EXTRA_DATA);
            status=intent.getExtras().getString(Constant.ARGS_PARAM1);
            updateSingle(position);
//            initData(true);//刷新整个页面
        }
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.my_loan));
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        adapter=new MyLoanListAdapter(this);
        lv.setAdapter(adapter);
        initData(true);
        initListener();
    }

    private void initData(boolean isShow) {
        isRefrensh=true;
        currentPage=1;
        NetApi.myLoanList(MyLoanActivity.this, isShow, SPUtil.getToken(), currentPage, Constant.PAGE_SIZE,loanListObserver );
    }
    private void reLoadData() {
        isRefrensh=false;
        NetApi.myLoanList(MyLoanActivity.this, false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, loanListObserver);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshlayout -> initData(false));
        refreshLayout.setOnLoadMoreListener(refreshlayout -> reLoadData());
    }

    //局部刷新
    private void updateSingle(int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lv.getChildAt(position - firstVisiblePosition);
            adapter.getItem(position).status=status;//刷新此条item model 不包括头布局
            FrameLayout layoutOperation=view.findViewById(R.id.item_myLoan_layoutOperation);
            TextView tvStatus=view.findViewById(R.id.item_myLoan_tvStatus);
            layoutOperation.setVisibility(View.GONE);
            tvStatus.setText(getStatus(Integer.parseInt(status)));

        }
    }


    //单条局部刷新
    @Override
    public void notifyListViewPos(int pos) {
        updateSingle(pos);
    }

    //状态   -2：募集失败（逾期）；-1募集失败；0 募集中;1募集成功，待还款；11提前还款；
    // 12正常还款；13逾期还款；14.还款完成；16强制交割；17：强制平仓?
    private String getStatus(int status){
        //1 两个按钮  0隐藏去还款
        if(status==-2){
            return getString(R.string.within_the_time_limit);
        }else if(status==-1){
            return getString(R.string.raise_failed);
        }else if(status==0){
            return getString(R.string.raiseing);
        }else if(status==1){
            return getString(R.string.wait_repay);
        }else if(status==11){
            return getString(R.string.prepayment);
        }else if(status==12){
            return getString(R.string.repay_nomal);
        }else if(status==13){
            return getString(R.string.repay_within_the_time_limit);
        }else if(status==14){
            return getString(R.string.repay_finish);
        }else if(status==16){
            return getString(R.string.mandatory_delivery);
        }else if(status==-1){
            return getString(R.string.forced_to_unwind);
        }else{
            return "";
        }
    }


    Observer<ResultBean<List<MyLoanList>>> loanListObserver = new NetClient.RxObserver<ResultBean<List<MyLoanList>>>() {
        @Override
        public void onSuccess(ResultBean<List<MyLoanList>> listResultBean) {
            if (listResultBean == null) return;
            if (listResultBean.isSuccess()) {
                if(listResultBean.data!=null&&listResultBean.data.size()>0){
                    if(isRefrensh)
                        adapter.loadData(listResultBean.data);
                    else
                        adapter.reLoadData(listResultBean.data);
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

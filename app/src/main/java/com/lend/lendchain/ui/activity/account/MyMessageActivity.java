package com.lend.lendchain.ui.activity.account;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MessageList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.adapter.MyMessageAdapter;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.IsInstallWeChatOrAliPay;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 我的消息
 */
public class MyMessageActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.my_message_lv)
    ListViewWithOptional lv;
    @BindView(R.id.my_message_weChat)
    FrameLayout weChat;
    private MyMessageAdapter adapter;
    private int currentPage=1;
    private boolean isRefrensh=false;
    private int type=1;//1未读  2已读
    private Dialog dialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        //不顶到状态栏 修改颜色
        StatusBarUtil.setStatusBarColor(MyMessageActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(MyMessageActivity.this);
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        baseTitleBar.setTitle(getString(R.string.my_message_unread));
        baseTitleBar.setTvRightText(getString(R.string.read));
        baseTitleBar.getTvRightText().setTextColor(ColorUtils.COLOR_509FFF);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutCommon(refreshLayout);
        adapter=new MyMessageAdapter(this);
        lv.setAdapter(adapter);
        initData(true);
        initListener();
    }
    //下拉刷新
    private void initListener() {
        refreshLayout.setOnRefreshListener(refreshLayout -> initData(false));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> reloadData());
        tvRightTitle.setOnClickListener(v -> {
            type=type==1?2:1;
            initData(true);
        });
        weChat.setOnClickListener(v -> {
            CommonUtil.clipboardStr("Lendx2018");
            if(dialog==null){
                CustomDialog.Builder builder=new CustomDialog.Builder(MyMessageActivity.this);
                builder.setMessage(getString(R.string.wechat_service_tip));
                builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setPositiveButton(getString(R.string.confirm_ding), (dialog, which) -> {
                    dialog.dismiss();
                    //跳转微信
                    if(IsInstallWeChatOrAliPay.isWeixinAvilible(MyMessageActivity.this)){
                        IsInstallWeChatOrAliPay.goWechatApi(MyMessageActivity.this);
                    }else{
                        //未安装微信
                        TipsToast.showTips(getString(R.string.please_install_wechat));
                    }
                });
                dialog=builder.create();
                builder.getContentMessage().setTextColor(ColorUtils.COLOR_000000);
                builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
                builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
            }
            dialog.show();
        });
    }

    private void initData(boolean isShow) {
        baseTitleBar.setTitle(getString(type==1?R.string.my_message_unread:R.string.my_message_read));
        baseTitleBar.setTvRightText(getString(type==1?R.string.read:R.string.unread));
        currentPage=1;
        isRefrensh=true;
        NetApi.messageList(this,isShow,SPUtil.getToken(),currentPage, Constant.PAGE_SIZE,type,observer);
    }
    private void reloadData(){
        isRefrensh=false;
        NetApi.messageList(this,false,SPUtil.getToken(),++currentPage, Constant.PAGE_SIZE,type,observer);
    }

    Observer<ResultBean<MessageList>> observer=new NetClient.RxObserver<ResultBean<MessageList>>() {
        @Override
        public void onSuccess(ResultBean<MessageList> listResultBean) {
            if(listResultBean==null)return;
            if(listResultBean.isSuccess()){
                if(listResultBean.data.records!=null&&listResultBean.data.records.size()>0){
                    if(isRefrensh)
                        adapter.loadData(listResultBean.data.records,type);
                    else
                        adapter.reLoadData(listResultBean.data.records,type);
                }else{
                    if(currentPage==1){//一条数据都没有显示空数据
                        adapter.clearData();
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    }else{//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }else{
                setHttpFailed(MyMessageActivity.this,listResultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            finishRefrensh();
            TipsToast.showTips(getString(R.string.netWorkError));
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            finishRefrensh();
        }
    };

    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData(true);//返回刷新
    }
}

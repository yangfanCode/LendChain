package com.lend.lendchain.ui.activity.account;

import android.os.Bundle;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MessageList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.common.CustomServiceActivity;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.utils.StatusBarUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class MyMessageDetailActivity extends BaseActivity {

    @BindView(R.id.my_message_detail_tvMessage)
    TextView tvMessage;
    @BindView(R.id.item_my_message_detail_tvTitle)
    TextView tvTitle;
    @BindView(R.id.item_my_message_detail_tvTime)
    TextView tvTime;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String id,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message_detail);
        //不顶到状态栏 修改颜色
        StatusBarUtil.setStatusBarColor(MyMessageDetailActivity.this, R.color.white);
        StatusBarUtil.StatusBarLightMode(MyMessageDetailActivity.this);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        id=getIntent().getExtras().getString(Constant.INTENT_EXTRA_DATA);
        title=getIntent().getExtras().getString(Constant.ARGS_PARAM1);
        baseTitleBar.setTitle(title);
        baseTitleBar.setLayLeftBackClickListener(v -> finish());
        baseTitleBar.setShareImageResource(R.mipmap.icon_service_pre);
        baseTitleBar.setImvShareClickListener(v -> CommonUtils.openActicity(this, CustomServiceActivity.class,null));
        //设置回弹属性
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        initData();
    }

    private void initData() {
        NetApi.messageDetail(this,true, SPUtil.getToken(),id,observer);
    }

    Observer<ResultBean<MessageList.Item>> observer=new NetClient.RxObserver<ResultBean<MessageList.Item>>() {
        @Override
        public void onSuccess(ResultBean<MessageList.Item> messageListResultBean) {
            if(messageListResultBean==null)return;
            if(messageListResultBean.isSuccess()){
                tvTime.setText(TimeUtils.getDateToStringMs(messageListResultBean.data.ctime,TimeUtils.YYYY_MM_dd_HH_MM_SS));
                tvTitle.setText(messageListResultBean.data.title);
                tvMessage.setText(messageListResultBean.data.content);
            }else{
                setHttpFailed(MyMessageDetailActivity.this,messageListResultBean);
            }
        }
    };
}

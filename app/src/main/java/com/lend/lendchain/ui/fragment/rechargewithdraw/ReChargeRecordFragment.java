package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.CoinList;
import com.lend.lendchain.bean.GetBlockCityRecharge;
import com.lend.lendchain.bean.MessageEvent;
import com.lend.lendchain.bean.Recharge;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.helper.RxBus;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.network.subscriber.SafeOnlyNextSubscriber;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.fragment.rechargewithdraw.adapter.RechargeAdapter;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.widget.ListViewWithOptional;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.TipsToast;
import com.lvfq.pickerview.OptionsPickerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yangfan.widget.CustomDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReChargeRecordFragment extends Fragment {
    @BindView(R.id.recharge_record_lv)
    ListViewWithOptional lv;
    @BindView(R.id.recharge_record_btnSpeedUp)
    TextView btnSpeedUp;
    @BindView(R.id.recharge_speedup_layout)
    FrameLayout layoutSpeed;
    @BindView(R.id.dialog_recharge_speed_etHash)
    EditText etHash;
    @BindView(R.id.dialog_recharge_speed_layoutCoinSelect)
    FrameLayout layoutCoin;
    @BindView(R.id.dialog_recharge_speed_tvCoin)
    TextView tvCoin;
    @BindView(R.id.dialog_recharge_speed_ivClose)
    ImageView ivClose;
    @BindView(R.id.dialog_recharge_speed_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View parentView;
    private int currentPage = 1;
    private boolean isRefrensh = true;
    private RechargeAdapter adapter;
    private OptionsPickerView optionsPickerView = null;
    private List<CoinList> list;
    private ArrayList<String> coins;//picker 数据
    private Dialog dialog = null;
    private PopupWindow popupWindow = null;
    private int coinsPos = 0;//picker选择的pos
    private Subscription rxSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_re_charge_record, container,
                    false);
            initView();
            initData(true);
            initListener();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }


    public static ReChargeRecordFragment newInstance() {
        ReChargeRecordFragment fragment = new ReChargeRecordFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rxSubscription == null || rxSubscription.isUnsubscribed()) {
            getRxBus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    private void initView() {
        ButterKnife.bind(this, parentView);
        //初始化SmartRefrenshLayout属性
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutCommon(refreshLayout);
        adapter = new RechargeAdapter(getActivity());
        lv.setAdapter(adapter);
        coins = new ArrayList<>();
        list = new ArrayList<>();
    }

    public void initData(boolean isShow) {
        isRefrensh = true;
        currentPage = 1;
        NetApi.myReChargeList(getActivity(), isShow, SPUtil.getToken(), currentPage, Constant.PAGE_SIZE, rechargeRecordObserver);
    }

    private void reLoadData() {
        isRefrensh = false;
        NetApi.myReChargeList(getActivity(), false, SPUtil.getToken(), ++currentPage, Constant.PAGE_SIZE, rechargeRecordObserver);
    }

    private void getRxBus() {
        rxSubscription = RxBus.getInstance().toObserverable(MessageEvent.class).subscribe(new SafeOnlyNextSubscriber<MessageEvent>() {
            @Override
            public void onNext(MessageEvent args) {
                super.onNext(args);
                int type = args.type;
                if (type == MessageEvent.RECHARGE_BLOCKCITY_GOPAY) {//点击去支付
                    String orderId = (String) args.data;
                    NetApi.getBlockCityRecharge(getActivity(), true, SPUtil.getToken(), orderId, getBlockObserver);
                } else if (type == MessageEvent.RECHARGE_BLOCKCITY_COUNTDOWN) {//倒计时结束刷新列表
                    initData(true);
                } else if (type == MessageEvent.RECHARGE_BLOCKCITY_CLOSE) {//关闭订单
                    String orderId = (String) args.data;
                    showCloseOrderDialog(orderId);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getRxBus();//rxbus报错后 会自动取消订阅 在error重新订阅
            }
        });
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
        btnSpeedUp.setOnClickListener(v -> {
            NetApi.getCoinList(getActivity(), true, SPUtil.getToken(), coinListObserver);
        });
        ivClose.setOnClickListener(v -> {
            layoutSpeed.setVisibility(View.GONE);
            btnSpeedUp.setEnabled(true);//关闭充值加速时允许点击
            setDeafultState();
        });
        //充值加速确定
        btnConfirm.setOnClickListener(v -> {
            String coin = coins.get(coinsPos);
            String hash = etHash.getText().toString().trim();
            if (tvCoin.getText().equals(getString(R.string.coin_select))) {
                TipsToast.showTips(getString(R.string.please_select_coin));
                return;
            }
            if (TextUtils.isEmpty(hash)) {
                TipsToast.showTips(getString(R.string.please_input_recharge_hash));
                return;
            }
//            if("ETH".equals(coin)||"QKC".equals(coin)||"DACC".equals(coin)){
//                if(hash.length()!=66||!hash.startsWith("0x")||!regexHash(hash)){
//                    TipsToast.showTips(getString(R.string.hash_tips1));
//                    return;
//                }
//            }else{
//                if(hash.length()!=64||!regexHash(hash)){
//                    TipsToast.showTips(getString(R.string.hash_tips2));
//                    return;
//                }
//            }
            NetApi.rechargeSpeedCreate(getActivity(), true, SPUtil.getToken(), String.valueOf(list.get(coinsPos).uniqueId), hash, rechargeSpeedObserver);
        });
    }

    //删除GXS LV币种
    private void setCoinListData() {
        coins.clear();
        for (Iterator<CoinList> it = list.iterator(); it.hasNext(); ) {
            CoinList coinList = it.next();
            if ("GXC".equals(coinList.cyptoCode) || "LV".equals(coinList.cyptoCode)) {
                it.remove();
                continue;
            }
            coins.add(coinList.cyptoCode);
        }
    }

    //正则校验
    private boolean regexHash(String hash) {
        return Pattern.matches("^(?![0-9]+$)(?![a-z]+$)[0-9a-z]{64,66}$", hash);
    }

    Observer<ResultBean<List<Recharge>>> rechargeRecordObserver = new NetClient.RxObserver<ResultBean<List<Recharge>>>() {
        @Override
        public void onSuccess(ResultBean<List<Recharge>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null && resultBean.data.size() > 0) {
                    if (isRefrensh)
                        adapter.loadData(resultBean.data);
                    else
                        adapter.reLoadData(resultBean.data);
                } else {
                    if (currentPage == 1) {//一条数据都没有显示空数据
                        lv.setEmptyView(OptionalLayout.TypeEnum.NO_DATA);
                    } else {//上拉到底展示没有更多数据
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            } else {
                ((BaseActivity) getActivity()).setHttpFailed(getActivity(), resultBean);
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

    Observer<ResultBean<List<CoinList>>> coinListObserver = new NetClient.RxObserver<ResultBean<List<CoinList>>>() {
        @Override
        public void onSuccess(ResultBean<List<CoinList>> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                list.clear();
                list.addAll(resultBean.data);
                setCoinListData();//删除数据
                layoutSpeed.setVisibility(View.VISIBLE);
                btnSpeedUp.setEnabled(false);//展示充值加速时屏蔽点击
                layoutCoin.setOnClickListener(v -> {
                    if (optionsPickerView == null) {
                        optionsPickerView = new OptionsPickerView(getActivity());
                        optionsPickerView.setOnoptionsSelectListener((options1, option2, options3) -> {
                            coinsPos = options1;
                            tvCoin.setText(coins.get(options1));
                            etHash.getText().clear();
                        });
                        optionsPickerView.setCancelable(true);
                        optionsPickerView.setPicker(coins);
                        optionsPickerView.setTextSize(Constant.PICKER_VIEW_TEXT_SIZE);
                        optionsPickerView.setLineSpacingMultiplier(Constant.PICKER_VIEW_MULTIPLIER);
                    }
                    optionsPickerView.setSelectOptions(coins.indexOf(tvCoin.getText().toString()) != -1 ? coins.indexOf(tvCoin.getText().toString()) : 0);
                    optionsPickerView.show();

                });
            } else {
                TipsToast.showTips(resultBean.message);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

    Observer<ResultBean> rechargeSpeedObserver = new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {//加速成功
                TipsToast.showTips(getString(R.string.recharge_speedup_success));
                layoutSpeed.setVisibility(View.GONE);
                btnSpeedUp.setEnabled(true);//关闭充值加速时允许点击
                setDeafultState();
            } else if ("-1".equals(resultBean.code)) {//报错提示
                TipsToast.showTips(resultBean.message);
//                layoutSpeed.setVisibility(View.GONE);
//                btnSpeedUp.setEnabled(true);//关闭充值加速时允许点击
//                setDeafultState();
            } else {
                ((BaseActivity) getActivity()).setHttpFailed(getActivity(), resultBean);
            }
        }
    };

    //查询布洛克城充值信息
    Observer<ResultBean<GetBlockCityRecharge>> getBlockObserver = new NetClient.RxObserver<ResultBean<GetBlockCityRecharge>>() {
        @Override
        public void onSuccess(ResultBean<GetBlockCityRecharge> getBlockCityRechargeResultBean) {
            if (getBlockCityRechargeResultBean == null) return;
            if (getBlockCityRechargeResultBean.isSuccess()) {
                //跳转布洛克城支付
                String tradeNo = getBlockCityRechargeResultBean.data.tradeNo;
                String orderId = getBlockCityRechargeResultBean.data.orderId;//订单
                //跳转布洛克城支付
                try {
                    Uri uri = Uri.parse("blockcity://pay?tradeNo=" + tradeNo + "&callbackUrl=" + URLEncoder.encode("lendchain://pay/result?orderId=" + orderId + "split", "UTF-8"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                ((BaseActivity) getActivity()).setHttpFailed(getActivity(), getBlockCityRechargeResultBean);
            }
        }
    };
    Observer<ResultBean> closeOrderObserver = new NetClient.RxObserver<ResultBean>() {
        @Override
        public void onSuccess(ResultBean resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                TipsToast.showTips(getString(R.string.order_close_success));
                initData(false);
            }
        }
    };

    //充值加速设置默认状态
    private void setDeafultState() {
        etHash.getText().clear();
        tvCoin.setText(getString(R.string.coin_select));//展示必中选择
        coinsPos = 0;
    }

    private void finishRefrensh() {
        refreshLayout.finishRefresh();//传入false表示加载失败
        refreshLayout.finishLoadMore();//传入false表示加载失败
    }

    //关闭订单
    private void showCloseOrderDialog(String orderId) {
        if (dialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.close_order_tips));
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(getString(R.string.confirm_ding), (dialog, which) -> {
                NetApi.closeOrderRecharge(getActivity(), true, SPUtil.getToken(),orderId, closeOrderObserver);
                dialog.dismiss();
            });
            dialog = builder.create();
            builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
            builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
            dialog.setCancelable(false);
        }
        dialog.show();
    }
}

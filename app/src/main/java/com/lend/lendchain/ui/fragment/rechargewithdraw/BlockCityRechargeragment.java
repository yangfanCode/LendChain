package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.DecimalDigitsEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 布洛克城充值
 */
public class BlockCityRechargeragment extends Fragment {
    @BindView(R.id.blockcity_recharge_etCount)
    DecimalDigitsEditText etCount;
    @BindView(R.id.blockcity_recharge_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.blockcity_recharge_tvCoin)
    TextView tvCoin;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private View parentView;
    private String code, cryptoId, tradeNo, orderId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_block_cityragment, container,
                    false);
            initView();
            initData();
            initListener();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    public static BlockCityRechargeragment newInstance(String param1, String param2) {
        BlockCityRechargeragment fragment = new BlockCityRechargeragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_PARAM1, param1);
        args.putString(Constant.ARGS_PARAM2, param2);
//        args.putString(Constant.ARGS_PARAM3, param1);
//        args.putString(Constant.ARGS_PARAM4, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void initView() {
        ButterKnife.bind(this, parentView);
        if (getArguments() != null) {
//            margin = getArguments().getString(Constant.ARGS_PARAM1);
//            memo = getArguments().getString(Constant.ARGS_PARAM2);
            code = getArguments().getString(Constant.ARGS_PARAM1);
            cryptoId = getArguments().getString(Constant.ARGS_PARAM2);
        }
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        initData();
        initListener();
    }


    private void initData() {
        tvCoin.setText(code);
    }


    private void initListener() {
        btnConfirm.setOnClickListener(v -> {
            String count = etCount.getText().toString().trim();
            if (TextUtils.isEmpty(count)) {
                TipsToast.showTips(getString(R.string.please_input_recharge_count));
                return;
            }
            NetApi.blockCityRecharge(getActivity(), true, SPUtil.getToken(), count, cryptoId, observer);
        });
    }

    Observer<ResultBean<SimpleBean>> observer = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                tradeNo = resultBean.data.tradeNo;
                orderId = resultBean.data.orderId;//订单
                //跳转布洛克城支付
                try {
                    Uri uri = Uri.parse("blockcity://pay?tradeNo="+tradeNo+"&callbackUrl="+ URLEncoder.encode("lendchain://pay/result?orderId="+orderId+ "split","UTF-8"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                ((BaseActivity) getActivity()).setHttpFailed(getActivity(), resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
        }
    };
}

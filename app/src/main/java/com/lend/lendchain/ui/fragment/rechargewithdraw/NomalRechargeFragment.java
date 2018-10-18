package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DisplayUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.QRCodeUtil;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * 数字钱包充值
 */
public class NomalRechargeFragment extends Fragment {
    @BindView(R.id.recharge_tvAddress)
    TextView tvAddress;
    @BindView(R.id.recharge_ivQRCode)
    ImageView ivQRCode;
    @BindView(R.id.recharge_tvCopy)
    TextView tvCopy;
    @BindView(R.id.recharge_tvCopyM)
    TextView tvCopyM;
    @BindView(R.id.recharge_tvMemo)
    TextView tvMemo;
    @BindView(R.id.recharge_tvTips)
    TextView tvTips;
    @BindView(R.id.recharge_llMemo)
    LinearLayout llMemo;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String add , code, memo, cryptoId;//币种
    private View parentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_nomal_recharge, container,
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

    public static NomalRechargeFragment newInstance(String param1,String param2,String param3,String param4){
        NomalRechargeFragment fragment = new NomalRechargeFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_PARAM1, param1);
        args.putString(Constant.ARGS_PARAM2, param2);
        args.putString(Constant.ARGS_PARAM3, param3);
        args.putString(Constant.ARGS_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    public void initView() {
        ButterKnife.bind(this, parentView);
        if (getArguments() != null) {
            add = getArguments().getString(Constant.ARGS_PARAM1);
            memo = getArguments().getString(Constant.ARGS_PARAM2);
            code = getArguments().getString(Constant.ARGS_PARAM3);
            cryptoId = getArguments().getString(Constant.ARGS_PARAM4);
        }
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        initData();
        initListener();
    }


    private void initData() {
        if(TextUtils.isEmpty(add))add="(null)";
        NetApi.coinAttribute(getActivity(), true, SPUtil.getToken(), cryptoId, rechargeObserver);
        tvAddress.setText(add);
        llMemo.setVisibility(("GXS".equals(code) || "LV".equals(code)) ? View.VISIBLE : View.GONE);
        if (llMemo.getVisibility() == View.VISIBLE) tvMemo.setText(memo);

        //展示二维码
        ivQRCode.setImageBitmap(QRCodeUtil.createQrcode(add, DisplayUtil.dp2px(getActivity(), 95), 1));
    }


    private void initListener() {
        tvCopy.setOnClickListener(v -> {
            CommonUtil.clipboardStr(add);
            TipsToast.showTips(getString(R.string.copy_success));
        });
        tvCopyM.setOnClickListener(v -> {
            CommonUtil.clipboardStr(memo);
            TipsToast.showTips(getString(R.string.copy_success));
        });
    }

    Observer<ResultBean<SimpleBean>> rechargeObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @SuppressLint("StringFormatMatches")
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    tvTips.setText(String.format(getString(R.string.recharge_tips), code,resultBean.data.accountNum,resultBean.data.withdrawNum, DoubleUtils.doubleTransRound6(resultBean.data.minDeposit), code));
                }
            } else {
                ((BaseActivity)getActivity()).setHttpFailed(getActivity(),resultBean);
            }
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            TipsToast.showTips(getString(R.string.netWorkError));
        }
    };

}

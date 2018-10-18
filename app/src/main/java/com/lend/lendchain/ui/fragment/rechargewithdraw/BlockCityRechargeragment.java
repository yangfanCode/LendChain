package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.DecimalDigitsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private String code;
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

    public static BlockCityRechargeragment newInstance(String param1){
        BlockCityRechargeragment fragment = new BlockCityRechargeragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_PARAM1, param1);
//        args.putString(Constant.ARGS_PARAM2, param1);
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
//            cryptoId = getArguments().getString(Constant.ARGS_PARAM4);
        }
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        initData();
        initListener();
    }


    private void initData() {
        tvCoin.setText(code);
    }


    private void initListener() {
    }


}

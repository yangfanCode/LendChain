package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.Constant;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlockCityWithDrawFragment extends Fragment {
    private View parentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_block_city_with_draw, container,
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
    public static BlockCityWithDrawFragment newInstance(String param1,String param2,String param3,String param4){
        BlockCityWithDrawFragment fragment = new BlockCityWithDrawFragment();
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
//            margin = getArguments().getString(Constant.ARGS_PARAM1);
//            memo = getArguments().getString(Constant.ARGS_PARAM2);
//            code = getArguments().getString(Constant.ARGS_PARAM1);
//            cryptoId = getArguments().getString(Constant.ARGS_PARAM4);
        }
        initData();
        initListener();
    }


    private void initData() {
//        tvCoin.setText(code);
    }


    private void initListener() {
    }

}

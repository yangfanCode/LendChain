package com.lend.lendchain.ui.fragment.invest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullRefreshRecyclerViewV;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lend.lendchain.R;
import com.lend.lendchain.bean.AddRecordList;
import com.lend.lendchain.ui.fragment.invest.adapter.AddRecordAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.widget.RecycleViewDivider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 追加 子fragment
 */
public class AddRecordFragment extends Fragment {
    @BindView(R.id.invest_record_recylerView)
    PullRefreshRecyclerViewV recyclerViewV;
    @BindView(R.id.invest_record_tvAddAmount)
    TextView tvAddAmount;
    @BindView(R.id.invest_record_tvMortgageAmount)
    TextView tvMortgageAmount;
    private ArrayList<AddRecordList> list;
    private String code;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_record, container, false);
        initView();
        initData();
        return rootView;
    }

    public static AddRecordFragment newInstance(ArrayList<AddRecordList> param1, String code) {
        AddRecordFragment fragment = new AddRecordFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.INTENT_EXTRA_DATA, param1);
        args.putString(Constant.ARGS_PARAM1, code);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList(Constant.INTENT_EXTRA_DATA);
            code = getArguments().getString(Constant.ARGS_PARAM1);
        }
        recyclerViewV.getRefreshableView().addItemDecoration(new RecycleViewDivider(
                getContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.xc_group_divider)));
        recyclerViewV.setMode(PullToRefreshBase.Mode.DISABLED);

    }

    private void initData() {
        tvAddAmount.setText(getString(R.string.add_amount).concat("\n("+code+")"));
        tvMortgageAmount.setText(getString(R.string.mortgage_amount).concat("\n("+code+")"));
        AddRecordAdapter adapter = new AddRecordAdapter(getActivity(), list);
        recyclerViewV.setAdapter(adapter);
    }


}

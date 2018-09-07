package com.lend.lendchain.ui.fragment.invest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.InvestRecordList;
import com.lend.lendchain.ui.fragment.invest.adapter.InvestRecordAdapter;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.widget.OptionalLayout;
import com.lend.lendchain.widget.RecycleViewDivider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 已购记录 子fragment
 */
public class InvestRecordFragment extends Fragment {
    @BindView(R.id.invest_record_recylerView)
    RecyclerView recyclerViewV;
    @BindView(R.id.invest_record_tvAmount)
    TextView tvAmount;
    @BindView(R.id.optionalLayout)
    OptionalLayout optionalLayout;
    private ArrayList<InvestRecordList> list;
    private String code;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_invest_record, container, false);
            initView();
            initData();
        }
        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    public static InvestRecordFragment newInstance(ArrayList<InvestRecordList> param1, String code) {
        InvestRecordFragment fragment = new InvestRecordFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.INTENT_EXTRA_DATA, param1);
        args.putString(Constant.ARGS_PARAM1, code);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        ButterKnife.bind(this, parentView);
        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList(Constant.INTENT_EXTRA_DATA);
            code = getArguments().getString(Constant.ARGS_PARAM1);
        }
        LinearLayoutManager mannagerTwo = new LinearLayoutManager(getActivity());
        mannagerTwo.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewV.setLayoutManager(mannagerTwo);
        //添加分割线
        recyclerViewV.addItemDecoration(new RecycleViewDivider(
                getContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.xc_group_divider)));

    }

    private void initData() {
        tvAmount.setText(getString(R.string.amount).concat("("+code+")"));
        InvestRecordAdapter adapter = new InvestRecordAdapter(getActivity(), list);
        recyclerViewV.setAdapter(adapter);
        if(list.size()==0){//空数据页面
            optionalLayout.setTypeEnum(OptionalLayout.TypeEnum.NO_DATA);
            optionalLayout.setVisibility(View.VISIBLE);
        }
    }


}

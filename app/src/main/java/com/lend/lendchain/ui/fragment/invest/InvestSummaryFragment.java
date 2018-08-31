package com.lend.lendchain.ui.fragment.invest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投资详情子fragment
 */
public class InvestSummaryFragment extends Fragment {
    @BindView(R.id.invest_fragment_tvSummary)
    TextView tvSummary;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_invest_summary, container, false);
        initView();
        return rootView;
    }


    public static InvestSummaryFragment newInstance(String param1){
        InvestSummaryFragment fragment = new InvestSummaryFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            String desc = getArguments().getString(Constant.ARGS_PARAM1);
            tvSummary.setText(desc);
        }
    }
}

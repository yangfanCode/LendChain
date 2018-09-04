package com.lend.lendchain.ui.fragment.rechargewithdraw;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lend.lendchain.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LVSendFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lvsend, container, false);
    }

    public static LVSendFragment newInstance(){
        LVSendFragment fragment = new LVSendFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

}

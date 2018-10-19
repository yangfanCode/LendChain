package com.lend.lendchain.ui.fragment.rechargewithdraw;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.SimpleBean;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.BaseActivity;
import com.lend.lendchain.ui.activity.account.rechargewithdraw.WithDrawCertifyActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.KeyBordUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.SmartRefrenshLayoutUtils;
import com.lend.lendchain.widget.TipsToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yangfan.widget.DecimalDigitsEditText;
import com.yangfan.widget.FormNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

/**
 *数字钱包提现
 */
public class NomalWithDrawFragment extends Fragment {
    //    @BindView(R.id.withdraw_tvPaste)
//    TextView tvPaste;
    @BindView(R.id.withdraw_etAdd)
    EditText etAdd;
    @BindView(R.id.withdraw_etCount)
    DecimalDigitsEditText etCount;
    @BindView(R.id.withdraw_etMemo)
    EditText etMemo;
    @BindView(R.id.withdraw_btnConfirm)
    TextView btnConfirm;
    @BindView(R.id.withdraw_tvPoundage)
    TextView tvPoundage;
    @BindView(R.id.withdraw_tvRealMoney)
    TextView tvRealMoney;
    @BindView(R.id.withdraw_tvRealMoneyCoin)
    TextView tvRealMoneyCoin;
    @BindView(R.id.withdraw_tvPoundageCoin)
    TextView tvPoundageCoin;
    @BindView(R.id.withdraw_tvLimit)
    TextView tvLimit;
    @BindView(R.id.withdraw_tvCoin)
    TextView tvCoin;
    @BindView(R.id.with_draw_llMemo)
    LinearLayout llMemo;
    @BindView(R.id.withdraw_fnOver)
    FormNormal fnOver;
    @BindView(R.id.withdraw_tvOverWithDraw)
    TextView tvOverWithDraw;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private String cryptoId, cryptoCode, id, count;
    private double withdrawFee, minWithdraw;//手续费
    private Dialog dialog = null;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_nomal_with_draw, container,
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
    public static NomalWithDrawFragment newInstance(String param1, String param2, String param3, String param4) {
        NomalWithDrawFragment fragment = new NomalWithDrawFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_PARAM1, param1);
        args.putString(Constant.ARGS_PARAM2, param2);
        args.putString(Constant.ARGS_PARAM3, param3);
        args.putString(Constant.ARGS_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    private void initView(){
        ButterKnife.bind(this, parentView);
        if(getArguments()!=null){
            cryptoId = getArguments().getString(Constant.ARGS_PARAM1);
            cryptoCode = getArguments().getString(Constant.ARGS_PARAM2);
            id = getArguments().getString(Constant.ARGS_PARAM3);
            count = getArguments().getString(Constant.ARGS_PARAM4);
        }
        SmartRefrenshLayoutUtils.getInstance().setSmartRefrenshLayoutDrag(refreshLayout);
        etCount.setAfterDot(("LV".equals(cryptoCode) || "GXS".equals(cryptoCode)) ?5:6);//lv gxs 5位小数
        llMemo.setVisibility(("LV".equals(cryptoCode) || "GXS".equals(cryptoCode)) ? View.VISIBLE : View.GONE);
        tvRealMoneyCoin.setText(cryptoCode);
        tvPoundageCoin.setText(cryptoCode);
        tvCoin.setText(cryptoCode);
        fnOver.setText(count.concat(" "+cryptoCode));//可用余额
        KeyBordUtils.setEditTextNoFocus(etAdd);
    }

    private void initData(){
        NetApi.coinAttribute(getActivity(), true, SPUtil.getToken(), cryptoId, withDrawObserver);
    }
    private void initListener() {
//        tvPaste.setOnClickListener(v -> {
//            String str = CommonUtil.clipboardPasteStr();
//            if (!TextUtils.isEmpty(str)) {
//                etAdd.setText(str);
//            }
//        });
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!TextUtils.isEmpty(input) && !input.endsWith(".") && Double.valueOf(input) >= minWithdraw) {
                    tvRealMoney.setText(DoubleUtils.doubleTransRound6(Double.parseDouble(input) - withdrawFee));
                } else {
                    tvRealMoney.setText("0");
                }
            }
        });
        tvOverWithDraw.setOnClickListener(v -> etCount.setText(count));
        btnConfirm.setOnClickListener(v -> {
            String address = etAdd.getText().toString().trim();
            String count = etCount.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                TipsToast.showTips(getString(R.string.please_input_withdraw_add));
                return;
            }
            if (TextUtils.isEmpty(count)) {
                TipsToast.showTips(getString(R.string.please_input_withdraw_count));
                return;
            }
            if (!TextUtils.isEmpty(count) && Double.valueOf(count) < minWithdraw) {
                TipsToast.showTips(getString(R.string.withdraw_min_count) + DoubleUtils.doubleTransRound6(minWithdraw));
                return;
            }
            //读取用户此币种余额 是否不足
            NetApi.getCoinCount(getActivity(),false, SPUtil.getToken(), Integer.valueOf(cryptoId), getCoinCountObserver);
        });
    }

    Observer<ResultBean<SimpleBean>> withDrawObserver = new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if (resultBean == null) return;
            if (resultBean.isSuccess()) {
                if (resultBean.data != null) {
                    withdrawFee = resultBean.data.withdrawFee;
                    minWithdraw = resultBean.data.minWithdraw;//最小提现
                    tvPoundage.setText(DoubleUtils.doubleTransRound6(withdrawFee));//手续费
                    etCount.setHint(getString(R.string.withdraw_min_count) + minWithdraw);
                    tvLimit.setText(String.format(tvLimit.getText().toString(), cryptoCode, String.valueOf(DoubleUtils.doubleTransRoundTwo(resultBean.data.dayWithdraw, 2))));//每日限额
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
    Observer<ResultBean<SimpleBean>> getCoinCountObserver=new NetClient.RxObserver<ResultBean<SimpleBean>>() {
        @Override
        public void onSuccess(ResultBean<SimpleBean> resultBean) {
            if(resultBean==null)return;
            if(resultBean.isSuccess()){
                if(resultBean.data!=null){
                    String count=etCount.getText().toString().trim();
                    double over=resultBean.data.amount;
                    if(Double.parseDouble(count)>over){
                        TipsToast.showTips(getString(R.string.over_lack));
                    }else{
                        String address = etAdd.getText().toString().trim();
                        String memo = etMemo.getText().toString().trim();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.ARGS_PARAM1, address);
                        bundle.putString(Constant.ARGS_PARAM2, count);
                        bundle.putString(Constant.ARGS_PARAM3, memo);
                        bundle.putString(Constant.ARGS_PARAM4, id);
                        CommonUtil.openActicity(getActivity(), WithDrawCertifyActivity.class, bundle);
                    }
                }
            }else{
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

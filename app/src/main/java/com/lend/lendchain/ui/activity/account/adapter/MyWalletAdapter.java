package com.lend.lendchain.ui.activity.account.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyWalletList;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.ui.activity.account.PlatFormTranshferActivity;
import com.lend.lendchain.ui.activity.account.rechargewithdraw.RechargeActivity;
import com.lend.lendchain.ui.activity.account.certify.SafeCertifyActivity;
import com.lend.lendchain.ui.activity.account.rechargewithdraw.WithDrawActivity;
import com.lend.lendchain.utils.CoinIconUtils;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.FrescoUtils;
import com.lend.lendchain.utils.SPUtil;
import com.yangfan.widget.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class MyWalletAdapter extends BaseAdapter {
    private Map<String, Integer> res = new HashMap<String, Integer>() {{
        put("BTC", R.mipmap.btc);
        put("ETH", R.mipmap.eth);
        put("GXC", R.mipmap.gxs);
        put("USDT", R.mipmap.usdt);
        put("LV", R.mipmap.lv);
        put("QKC", R.mipmap.qkc);
    }};
    private List<MyWalletList> list = new ArrayList<>();
    private Context context;
    private Dialog dialog = null;
    //充值点击
    private View.OnClickListener rechargeClick = v -> {
        String add = (String) v.getTag(R.id.str);
        String memo = (String) v.getTag(R.id.str1);
        String code = (String) v.getTag(R.id.str2);
        String cryptoId = (String) v.getTag(R.id.str3);
        int blockCityStatus= (int) v.getTag(R.id.str4);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_EXTRA_DATA, add);
        bundle.putString(Constant.ARGS_PARAM1, memo);
        bundle.putString(Constant.ARGS_PARAM2, code);
        bundle.putString(Constant.ARGS_PARAM3, cryptoId);
        bundle.putInt(Constant.ARGS_PARAM4, blockCityStatus);
        CommonUtil.openActicity(context, RechargeActivity.class, bundle);
    };
    //提现点击
    private View.OnClickListener withDrawClick = v -> {
//        if (SPUtil.getUserPhone() && SPUtil.getUserGoogle() && SPUtil.getUserKyc() == 2) {
            String cryptoId = (String) v.getTag(R.id.code);
            String code = (String) v.getTag(R.id.str);
            String id = (String) v.getTag(R.id.str1);
            String count = (String) v.getTag(R.id.str2);
            int blockCityStatus= (int) v.getTag(R.id.str3);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_DATA, cryptoId);
            bundle.putString(Constant.ARGS_PARAM1, code);
            bundle.putString(Constant.ARGS_PARAM2, id);
            bundle.putString(Constant.ARGS_PARAM3, count);
            bundle.putInt(Constant.ARGS_PARAM4, blockCityStatus);
            CommonUtil.openActicity(context, WithDrawActivity.class, bundle);
//        } else {
//            showCerfityDialog();
//        }
    };
    //转账
    private View.OnClickListener transferClick = v -> {
        if (SPUtil.getUserPhone() && SPUtil.getUserGoogle() && SPUtil.getUserKyc() == 2) {
            String cryptoId = (String) v.getTag(R.id.code);
            String code = (String) v.getTag(R.id.str);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.INTENT_EXTRA_DATA, cryptoId);
            bundle.putString(Constant.ARGS_PARAM1, code);
            CommonUtil.openActicity(context, PlatFormTranshferActivity.class, bundle);
        } else {
            showCerfityDialog();
        }
    };

    public MyWalletAdapter(Context context) {
        this.context = context;
    }

    public void loadData(List<MyWalletList> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<MyWalletList> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyWalletList myWalletList = list.get(position);
        ViewHolder viewHolder = ViewHolder.get(context, convertView, R.layout.item_my_wallet_lv);
        SimpleDraweeView ivIcon = viewHolder.getView(R.id.item_my_wallet_ivIcon);
        TextView tvCoinType = viewHolder.getView(R.id.item_my_wallet_tvCoinType);
        TextView tvCount = viewHolder.getView(R.id.item_my_wallet_tvCount);
        TextView tvRecharge = viewHolder.getView(R.id.item_my_wallet_tvRecharge);
        TextView tvWithdraw = viewHolder.getView(R.id.item_my_wallet_tvWithdraw);
        TextView tvTransfer = viewHolder.getView(R.id.item_my_wallet_tvTransfer);
        FrescoUtils.showThumb(ivIcon, CoinIconUtils.getInstance().getIcon(myWalletList.cryptoCode));//图片
        tvCoinType.setText(myWalletList.cryptoCode);//币种
        String count=DoubleUtils.doubleTransRound6(myWalletList.amount);//数量
        tvCount.setText(count);
        tvRecharge.setOnClickListener(rechargeClick);
        tvWithdraw.setOnClickListener(withDrawClick);
        tvTransfer.setOnClickListener(transferClick);
        tvRecharge.setTag(R.id.str2, myWalletList.cryptoCode);
        tvRecharge.setTag(R.id.str3, myWalletList.cryptoId);
        tvRecharge.setTag(R.id.str4, myWalletList.blockcityStatus);
        tvWithdraw.setTag(R.id.code, myWalletList.cryptoId);
        tvWithdraw.setTag(R.id.str, myWalletList.cryptoCode);
        tvWithdraw.setTag(R.id.str1, myWalletList.id);
        tvWithdraw.setTag(R.id.str2, count);
        tvWithdraw.setTag(R.id.str3, myWalletList.blockcityStatus);
        tvTransfer.setTag(R.id.code, myWalletList.cryptoId);
        tvTransfer.setTag(R.id.str, myWalletList.cryptoCode);
        if (myWalletList.depositAddrs != null) {
            tvRecharge.setTag(R.id.str, myWalletList.depositAddrs.addr);
            tvRecharge.setTag(R.id.str1, myWalletList.depositAddrs.memo);
        }
        return viewHolder.getConvertView();
    }

    //认证弹窗
    private void showCerfityDialog() {
        if (dialog == null) {
            CustomDialog.Builder builder = new CustomDialog.Builder(context);
            builder.setTitle(context.getString(R.string.warm_tips));
            builder.setMessage(context.getString(R.string.please_goto_certified_all_wallet));
            builder.setNegativeButton(context.getString(R.string.look_again), (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(context.getString(R.string.go_certified), (dialog, which) -> {
                CommonUtil.openActicity(context, SafeCertifyActivity.class, null);
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

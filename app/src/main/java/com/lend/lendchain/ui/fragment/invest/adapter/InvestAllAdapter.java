package com.lend.lendchain.ui.fragment.invest.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.InvestList;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.widget.GradientTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class InvestAllAdapter extends BaseAdapter {
    private List<InvestList> list = new ArrayList<>();
    private Context context;

    public InvestAllAdapter(Context context) {
        this.context = context;
    }

    public void loadData(List<InvestList> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<InvestList> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    @Override
    public InvestList getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).status == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InvestList investList = list.get(position);
        int type = getItemViewType(position);
        ViewHolder viewHolder = null;
        if (type == 0) {
            viewHolder = ViewHolder.get(context, convertView, R.layout.item_invest_style1);
        } else {
            viewHolder = ViewHolder.get(context, convertView, R.layout.item_invest_style2);
        }
        TextView tvType = viewHolder.getView(R.id.item_invest_tvType);//抵押标 平台标 除了抵押标都是平台标
        TextView tvName = viewHolder.getView(R.id.item_invest_tvName);
        TextView tvNickName = viewHolder.getView(R.id.item_invest_tvNickName);
        ImageView ivBorrowType = viewHolder.getView(R.id.item_invest_ivBorrowType);//borrowedType
        TextView tvDeadLine = viewHolder.getView(R.id.item_invest_tvDeadLine);//期限
        int borrowTypeId = investList.borrowTypeId;
        tvType.setBackgroundResource(getType(borrowTypeId));//背景
        tvType.setText(borrowTypeId == 1 ? context.getString(R.string.di3) : context.getString(R.string.ping2));//除了抵押都是平
        tvName.setText(investList.orderId);
        tvNickName.setVisibility(TextUtils.isEmpty(investList.nickname) ? View.GONE : View.VISIBLE);
        if (tvNickName.getVisibility() == View.VISIBLE) {
            tvNickName.setText(investList.nickname);//昵称
        }
        int borrowTypeRes = getBorrowTypeRes(borrowTypeId);//右上角标识
        ivBorrowType.setImageBitmap(borrowTypeRes == 0 ? null : BitmapFactory.decodeResource(context.getResources(), borrowTypeRes));
        tvDeadLine.setText(String.valueOf(investList.borrowDays).concat(context.getString(R.string.day)));
        if (type == 0) {
            GradientTextView tvdailyRate = viewHolder.getView(R.id.item_invest_tvdailyRate);//日利率
            tvdailyRate.setText(DoubleUtils.doubleRoundFormat(investList.interestRates * 360 * 100, 2) );
            TextView tvBorrowAmount = viewHolder.getView(R.id.item_invest_tvBorrowAmount);//借入资产
            tvBorrowAmount.setText(DoubleUtils.doubleTransRound6(investList.borrowAmount).concat(" "+investList.borrowCryptoCode));
            ProgressBar progressBar = viewHolder.getView(R.id.item_invest_pb);//进度条
            int progress = DoubleUtils.doubleToIntRound((investList.boughtAmount / investList.borrowAmount * 100));
            TextView tvPercent = viewHolder.getView(R.id.item_invest_tvPercent);//百分比
            progressBar.setProgress(progress);
            tvPercent.setText(DoubleUtils.doubleTransRoundTwo(investList.boughtAmount / investList.borrowAmount * 100, 2) + "%");//百分比
        }else{
            TextView tvdailyRate = viewHolder.getView(R.id.item_invest_tvdailyRate);//日利率
            tvdailyRate.setText(DoubleUtils.doubleRoundFormat(investList.interestRates * 360 * 100, 2) );
        }
        return viewHolder.getConvertView();
    }

    /**
     * 1 抵押标 2 预告 3平台标 4新手预告 5新手标
     * 设置右上角标签 就245有图
     * 23  2个格
     *
     * @param borrowTypeId
     * @return
     */
    private int getBorrowTypeRes(int borrowTypeId) {
        if (borrowTypeId == 2) {//预告
            return R.mipmap.icon_notice;
        } else if (borrowTypeId == 4) {//新手预告
            return R.mipmap.icon_novice_notice;
        } else if (borrowTypeId == 5) {//新手标
            return R.mipmap.icon_novice;
        } else {
            return 0;
        }
    }

    /**
     * 除了抵押标都是平台标
     *
     * @param borrowTypeId
     * @return
     */
    private int getType(int borrowTypeId) {
        if (borrowTypeId == 1) {//抵押标 2
            return R.drawable.bg_ff8e2e_radiu5;
        } else {//平台标
            return R.drawable.bg_509fff_radiu5;
        }
    }
}

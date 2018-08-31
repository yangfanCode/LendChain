package com.lend.lendchain.ui.fragment.invest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.InvestRecordList;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class InvestRecordAdapter extends RecyclerView.Adapter<InvestRecordAdapter.ViewHolder> {
    private List<InvestRecordList> list;
    private Context context;
    public InvestRecordAdapter(Context context, List<InvestRecordList>list){
        this.list=list;
        this.context=context;
    }

    @Override
    public InvestRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invest_record, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvestRecordList investRecordList=list.get(position);
        holder.tvName.setText(investRecordList.nickname);//昵称
        holder.tvAmount.setText(CommonUtil.doubleTransRound6(investRecordList.amount)+"");//金额
        holder.tvTime.setText(TimeUtils.getDateToStringS(investRecordList.time,TimeUtils.YYYY_MM_dd_HH_MM_SS1));
    }

    @Override
    public int getItemCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_invest_record_tvName)
        TextView tvName;
        @BindView(R.id.item_invest_record_tvAmount)
        TextView tvAmount;
        @BindView(R.id.item_invest_record_tvTime)
        TextView tvTime;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

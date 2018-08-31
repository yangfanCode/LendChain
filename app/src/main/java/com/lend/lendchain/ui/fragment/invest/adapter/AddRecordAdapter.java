package com.lend.lendchain.ui.fragment.invest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.AddRecordList;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class AddRecordAdapter extends RecyclerView.Adapter<AddRecordAdapter.ViewHolder> {
    private List<AddRecordList> list;
    private Context context;
    public AddRecordAdapter(Context context, List<AddRecordList>list){
        this.list=list;
        this.context=context;
    }

    @Override
    public AddRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_record, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddRecordList addRecordList=list.get(position);
        holder.tvTime.setText(TimeUtils.getDateToStringMs(addRecordList.ctime,TimeUtils.YYYY_MM_dd_HH_MM_SS1));//时间
        holder.tvAddAmount.setText(CommonUtil.doubleTransRound6(addRecordList.amount));//追加金额
        holder.tvMortgageAmount.setText(CommonUtil.doubleTransRound6(addRecordList.preAmount));//抵押总额
    }

    @Override
    public int getItemCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_invest_record_tvTime)
        TextView tvTime;
        @BindView(R.id.item_invest_record_tvAddAmount)
        TextView tvAddAmount;
        @BindView(R.id.item_invest_record_tvMortgageAmount)
        TextView tvMortgageAmount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

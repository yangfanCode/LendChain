package com.lend.lendchain.ui.activity.account.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.ChangeList;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class ChangeListAdapter extends BaseAdapter {
    private Context context;
    private List<ChangeList.Detail>list=new ArrayList<>();
    public ChangeListAdapter(Context context){
        this.context=context;
    }
    public void loadData(List<ChangeList.Detail>list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<ChangeList.Detail>list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
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
        ChangeList.Detail detail=list.get(position);
        ViewHolder holder=ViewHolder.get(context,convertView, R.layout.item_change_list);
        TextView tvStatus=holder.getView(R.id.item_change_list_tvStatus);
        TextView tvTime=holder.getView(R.id.item_change_list_tvTime);
        TextView tvOutAmount=holder.getView(R.id.item_change_list_tvOutAmount);
        TextView tvInAmount=holder.getView(R.id.item_change_list_tvInAmount);
        tvStatus.setText(getStatus(detail.status));
        tvTime.setText(TimeUtils.getDateToStringMs(Long.valueOf(detail.exchangeTime),TimeUtils.YYYY_MM_dd_HH_MM_SS1));
        tvOutAmount.setText(detail.depositAmount+" "+detail.depositCoinCode);//转出金额
        tvInAmount.setText(detail.realReceiveAmount+" "+detail.receiveCoinCode);//转出金额
        return holder.getConvertView();
    }

    private String getStatus(int status){
        if(status==1){
            return context.getString(R.string.processing);
        }else if(status==2){
            return context.getString(R.string.change_successful);
        }else if(status==3){
            return context.getString(R.string.change_failed);
        }
        return "";
    }
}

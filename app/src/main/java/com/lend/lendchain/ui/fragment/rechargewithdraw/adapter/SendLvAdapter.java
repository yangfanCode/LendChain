package com.lend.lendchain.ui.fragment.rechargewithdraw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.SendLvRecord;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class SendLvAdapter extends BaseAdapter {
    private Set<Integer> addPos=new LinkedHashSet<>();
    private List<SendLvRecord.Detail>list=new ArrayList<>();
    private Context context;
    public SendLvAdapter(Context context){
        this.context=context;
    }
    //点击显示隐藏
    private View.OnClickListener addOnClickListener= v -> {
        int position= (int) v.getTag(R.id.position);
        LinearLayout llAdd= (LinearLayout) v.getTag(R.id.view);
        if(llAdd.getVisibility()==View.VISIBLE){
            llAdd.setVisibility(View.GONE);
            addPos.remove(position);
        }else{
            llAdd.setVisibility(View.VISIBLE);
            addPos.add(position);
        }
    };

    public void loadData(List<SendLvRecord.Detail>list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<SendLvRecord.Detail>list){
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
        SendLvRecord.Detail detail=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_sendlv_record);
        TextView tvStatus=viewHolder.getView(R.id.sendLv_record_tvStatus);
        TextView tvCount=viewHolder.getView(R.id.sendLv_record_tvCount);
        TextView tvTime=viewHolder.getView(R.id.sendLv_record_tvTime);
        TextView tvOrderCode=viewHolder.getView(R.id.sendLv_record_tvOrderCode);
        TextView tvDesc=viewHolder.getView(R.id.sendLv_record_tvDesc);
        LinearLayout llOrderAdd=viewHolder.getView(R.id.sendLv_record_llOrderAdd);
        LinearLayout llOrder=viewHolder.getView(R.id.sendLv_record_llOrder);
        tvStatus.setText(detail.reason);//发放原因
        tvCount.setText("+"+DoubleUtils.doubleTransRound6(detail.amount)+" "+detail.cryptoCode);
        tvTime.setText(TimeUtils.getDateToStringMs(Long.parseLong(detail.time),"yyyy.MM.dd HH:mm:ss"));
        tvOrderCode.setText(context.getString(R.string.order_code)+":"+detail.id);//订单号
        tvDesc.setText(context.getString(R.string.send_LV_reason)+":"+detail.remark);//说明
        llOrder.setOnClickListener(addOnClickListener);
        llOrder.setTag(R.id.position,position);
        llOrder.setTag(R.id.view,llOrderAdd);
        llOrderAdd.setVisibility(addPos.contains(position)?View.VISIBLE:View.GONE);
        return viewHolder.getConvertView();
    }
}

package com.lend.lendchain.ui.fragment.rechargewithdraw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.RechargeWithDraw;
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
public class TransferRecordAdapter extends BaseAdapter {
    private Set<Integer> addPos=new LinkedHashSet<>();
    private List<RechargeWithDraw>list=new ArrayList<>();
    private Context context;
    public TransferRecordAdapter(Context context){
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

    public void loadData(List<RechargeWithDraw>list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<RechargeWithDraw>list){
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
        RechargeWithDraw rechargeWithDraw=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_transfer_record);
        TextView tvStatus=viewHolder.getView(R.id.transfer_record_tvStatus);
        TextView tvCount=viewHolder.getView(R.id.transfer_record_tvCount);
        TextView tvTime=viewHolder.getView(R.id.transfer_record_tvTime);
        TextView tvOrderCode=viewHolder.getView(R.id.transfer_record_tvOrderCode);
        TextView tvOrderAdd=viewHolder.getView(R.id.transfer_record_tvOrderAdd);
        LinearLayout llOrderAdd=viewHolder.getView(R.id.transfer_record_llOrderAdd);
        LinearLayout llOrder=viewHolder.getView(R.id.transfer_record_llOrder);
        int status=rechargeWithDraw.status;
//        tvStatus.setText(type==1?getRechargeStatus(status):getWithDrawStatus(status));
        tvCount.setText(DoubleUtils.doubleTransRound6(rechargeWithDraw.amount)+" "+rechargeWithDraw.cryptoCode);
        tvTime.setText(TimeUtils.getDateToStringS(Long.parseLong(rechargeWithDraw.ctime),"yyyy.MM.dd HH:mm:ss"));
        //订单号 充值 orderId 提现 txOrder
//        tvOrderCode.setText(context.getString(R.string.order_code)+":"+(type==1?rechargeWithDraw.orderId:rechargeWithDraw.txOrder));//订单号
        //地址 充值 addr addr 提现addrTo addr
//        String orderAddText=type==1?rechargeWithDraw.addr.addr:rechargeWithDraw.addrTo.addr;
//        tvOrderAdd.setText(type==1?context.getString(R.string.recharge_add):context.getString(R.string.withdraw_add)+":"+orderAddText);//充值地址
        llOrder.setOnClickListener(addOnClickListener);
        llOrder.setTag(R.id.position,position);
        llOrder.setTag(R.id.view,llOrderAdd);
        llOrderAdd.setVisibility(addPos.contains(position)?View.VISIBLE:View.GONE);
        return viewHolder.getConvertView();
    }
    //充值类型状态
    private String getRechargeStatus(int status){
        if(status==1){
            return context.getString(R.string.confirming);
        }else if(status==2){
            return context.getString(R.string.wait_in_account);
        }else if(status==3){
            return context.getString(R.string.in_accounted);
        }else{
            return "";
        }
    }
    //提现类型状态
    private String getWithDrawStatus(int status){
        if(status==0){
            return context.getString(R.string.wait_udit);
        }else if(status==1){
            return context.getString(R.string.processing);
        }else if(status==2){
            return context.getString(R.string.processed_finish);
        }else if(status==3){
            return context.getString(R.string.refuse);
        }else if(status==4){
            return context.getString(R.string.beyond_the_limit);
        }else{
            return "";
        }
    }
}

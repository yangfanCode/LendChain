package com.lend.lendchain.ui.fragment.rechargewithdraw.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.TransferRecord;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.utils.ColorUtils;
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
    private List<TransferRecord>list=new ArrayList<>();
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

    public void loadData(List<TransferRecord>list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<TransferRecord>list){
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
        TransferRecord transferRecord=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_transfer_record);
        TextView tvStatus=viewHolder.getView(R.id.transfer_record_tvStatus);
        TextView tvCount=viewHolder.getView(R.id.transfer_record_tvCount);
        TextView tvTime=viewHolder.getView(R.id.transfer_record_tvTime);
        TextView tvOrderCode=viewHolder.getView(R.id.transfer_record_tvOrderCode);
        TextView tvAcount=viewHolder.getView(R.id.transfer_record_tvAcount);
        LinearLayout llOrderAdd=viewHolder.getView(R.id.transfer_record_llOrderAdd);
        LinearLayout llOrder=viewHolder.getView(R.id.transfer_record_llOrder);
        int type=transferRecord.transgerType;
        tvStatus.setText(getTransferStatus(type));
        tvCount.setTextColor(setTransferCountColor(type));//字体颜色
        tvCount.setText((type==1?"+":"-")+ DoubleUtils.doubleTransRound6(transferRecord.transferAmount)+" "+transferRecord.code);//金额
        tvTime.setText(TimeUtils.getDateToStringMs(Long.parseLong(transferRecord.transferTime),"yyyy.MM.dd HH:mm:ss"));
        tvOrderCode.setText(context.getString(R.string.order_code)+":"+(TextUtils.isEmpty(transferRecord.transgerId)?"":transferRecord.transgerId));
        tvAcount.setText(getTransferAccount(type)+":"+transferRecord.transferAcc);//账户
        llOrder.setOnClickListener(addOnClickListener);
        llOrder.setTag(R.id.position,position);
        llOrder.setTag(R.id.view,llOrderAdd);
        llOrderAdd.setVisibility(addPos.contains(position)?View.VISIBLE:View.GONE);
        return viewHolder.getConvertView();
    }
    //转账类型状态
    private String getTransferStatus(int status){
        if(status==1){
            return context.getString(R.string.transferred_from_others_success);
        }else if(status==2){
            return context.getString(R.string.transferred_to_others_success);
        }else{
            return "";
        }
    }
    //充值类型状态
    private String getTransferAccount(int status){
        if(status==1){
            return context.getString(R.string.transferred_out_account);
        }else if(status==2){
            return context.getString(R.string.transferred_in_account);
        }else{
            return "";
        }
    }
    //字体颜色
    private int setTransferCountColor(int status){
        if(status==1){
            return ColorUtils.COLOR_FF6343;
        }else{
            return ColorUtils.COLOR_1ECC27;
        }
    }
}

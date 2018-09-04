package com.lend.lendchain.ui.activity.account.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyInvestList;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.ui.activity.invest.InvestSummaryActivity;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class MyInvestListAdapter extends BaseAdapter {
    private List<MyInvestList> list=new ArrayList<>();
    private Context context;
    //click name to summary
    private View.OnClickListener onClickListener=v -> {
        String id= (String) v.getTag(R.id.id);
        Bundle bundle = new Bundle();
        //传标的id
        bundle.putString(Constant.INTENT_EXTRA_DATA, id);
        CommonUtil.openActicity(context, InvestSummaryActivity.class, bundle);
    };
    public MyInvestListAdapter(Context context){
        this.context=context;

    }
    public void loadData(List<MyInvestList> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void reloadData(List<MyInvestList> list){
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
        MyInvestList myInvestList=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_my_invest_list);
        TextView tvName=viewHolder.getView(R.id.item_my_invest_list_tvName);
        TextView tvCreateTime=viewHolder.getView(R.id.item_my_invest_list_tvCreateTime);
        TextView tvStatus=viewHolder.getView(R.id.item_my_invest_list_tvStatus);
        TextView tvLoanAmount=viewHolder.getView(R.id.item_my_invest_list_tvLoanAmount);
        TextView tvInterest=viewHolder.getView(R.id.item_my_invest_list_tvInterest);
        TextView tvDeadLine=viewHolder.getView(R.id.item_my_invest_list_tvDeadLine);
        TextView tvRepayTime=viewHolder.getView(R.id.item_my_invest_list_tvRepayTime);
        tvName.setText(myInvestList.orderId);//name
        tvStatus.setText(getStatus(Integer.parseInt(myInvestList.status)));
        String code=myInvestList.cryptoCode;
        tvInterest.setText(DoubleUtils.doubleTransRound6(myInvestList.profit)+" "+code);
        tvCreateTime.setText(TimeUtils.getDateToStringS(myInvestList.ctime,TimeUtils.YYYY_MM_dd_HH_MM_SS1));
        tvLoanAmount.setText(DoubleUtils.doubleTransRound6(myInvestList.amount)+" "+code);
        tvDeadLine.setText(TextUtils.isEmpty(myInvestList.deadline)?"":TimeUtils.getDateToStringMs(Long.parseLong(myInvestList.deadline),"yyyy-MM-dd HH:mm:ss"));
        tvRepayTime.setText("0".equals(myInvestList.cutoffTime)?"":TimeUtils.getDateToStringMs(Long.parseLong(myInvestList.cutoffTime),"yyyy-MM-dd HH:mm:ss"));
        tvName.setOnClickListener(onClickListener);
        tvName.setTag(R.id.id,myInvestList.borrowId);
        return viewHolder.getConvertView();
    }

    private String getStatus(int status){
        if(status==0){
            return context.getString(R.string.wait_submit);
        }else if(status==1){
            return context.getString(R.string.success);
        }else if(status==2){
            return context.getString(R.string.wait_repay);
        }else if(status==3){
            return context.getString(R.string.within_the_time_limit);
        }else if(status==11){
            return context.getString(R.string.prepayment);
        }else if(status==12){
            return context.getString(R.string.repay_nomal);
        }else if(status==13){
            return context.getString(R.string.repay_within_the_time_limit);
        }else if(status==14){
            return context.getString(R.string.repay_finish);
        }else if(status==16){
            return context.getString(R.string.mandatory_delivery);
        }else if(status==17){
            return context.getString(R.string.forced_to_unwind);
        }else if(status==-1){
            return context.getString(R.string.fulled_standard);
        }else if(status==-2){
            return context.getString(R.string.invest_failed);
        }else{
            return context.getString(R.string.unknow);
        }
    }
}

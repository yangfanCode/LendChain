package com.lend.lendchain.ui.activity.account.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MyLoanList;
import com.lend.lendchain.bean.ResultBean;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.network.NetClient;
import com.lend.lendchain.network.api.NetApi;
import com.lend.lendchain.ui.activity.account.MyLoanSummaryActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.SPUtil;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.widget.TipsToast;
import com.yangfan.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class MyLoanListAdapter extends BaseAdapter {
    private List<MyLoanList> list=new ArrayList<>();
    private Context context;
    public MyLoanListAdapter(Context context){
        this.context=context;
    }
    //去还款
    private View.OnClickListener gotoRepayOnClickListener=v -> {
        String status="";
        MyLoanList myLoanList= (MyLoanList) v.getTag(R.id.object);
        CustomDialog.Builder builder=new CustomDialog.Builder(context);
        builder.setTitle(context.getString(R.string.warm_tips));
        if((Long.valueOf(myLoanList.deadline)*1000)<System.currentTimeMillis()){
            builder.setMessage(context.getString(R.string.confirm_within_repay));
            status="13";
        }else if((Long.valueOf(myLoanList.deadline)*1000)==System.currentTimeMillis()){
            builder.setMessage(context.getString(R.string.confirm_nomal_repay));
            status="12";
        }else{
            builder.setMessage(context.getString(R.string.confirm_before_repay));
            status="11";
        }
        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        String finalStatus = status;
        builder.setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
            NetApi.payBack(context, true, SPUtil.getToken(), myLoanList.id, new NetClient.RxObserver<ResultBean>() {
                @Override
                public void onSuccess(ResultBean resultBean) {
                    if(resultBean==null)return;
                    if(resultBean.isSuccess()){
                        TipsToast.showTips(context.getString(R.string.repay_success));
                        myLoanList.status= finalStatus;
                        notifyDataSetChanged();//还款之后刷新数据
                    }else{
                        TipsToast.showTips(resultBean.message);
                    }
                    dialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    TipsToast.showTips(context.getString(R.string.netWorkError));
                    dialog.dismiss();
                }
            });
        });
        Dialog dialog=builder.create();
        builder.getNegativeButton().setTextColor(ColorUtils.COLOR_509FFF);
        builder.getPositiveButton().setTextColor(ColorUtils.COLOR_509FFF);
        dialog.show();
        dialog.setCancelable(false);
    };
    //点击查看详情
    private View.OnClickListener lookMorelickListener=v -> {
        int position= (int) v.getTag(R.id.position);
        String brrowId= (String) v.getTag(R.id.str);
        String deadline= (String) v.getTag(R.id.str1);
        String quotaFullTime= (String) v.getTag(R.id.str2);
        String realPaybackTime= (String) v.getTag(R.id.str3);
        String status= (String) v.getTag(R.id.str4);
        String interest= (String) v.getTag(R.id.str5);
        Bundle bundle=new Bundle();
        bundle.putString(Constant.INTENT_EXTRA_DATA,brrowId);
        bundle.putString(Constant.ARGS_PARAM1,deadline);
        bundle.putString(Constant.ARGS_PARAM2,quotaFullTime);
        bundle.putString(Constant.ARGS_PARAM3,realPaybackTime);
        bundle.putString(Constant.ARGS_PARAM4,status);
        bundle.putString(Constant.ARGS_PARAM5,interest);
        bundle.putInt(Constant.ARGS_PARAM6,position);
        CommonUtil.openActicity(context, MyLoanSummaryActivity.class,bundle);
    };
    public void loadData(List<MyLoanList> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public void reLoadData(List<MyLoanList> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public MyLoanList getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyLoanList myLoanList=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_myloan_list);
        TextView tvCode=viewHolder.getView(R.id.item_myLoan_tvCode);
        TextView tvTime=viewHolder.getView(R.id.item_myLoan_tvTime);
        TextView tvStatus=viewHolder.getView(R.id.item_myLoan_tvStatus);
        TextView tvLoanCount=viewHolder.getView(R.id.item_myLoan_tvLoanCount);
        TextView tvInterest=viewHolder.getView(R.id.item_myLoan_tvInterest);
        TextView tvDeadLine=viewHolder.getView(R.id.item_myLoan_tvDeadLine);
        TextView tvOperation=viewHolder.getView(R.id.item_myLoan_tvOperation);
        FrameLayout layoutOperation=viewHolder.getView(R.id.item_myLoan_layoutOperation);
        tvCode.setText(myLoanList.orderId);//标名字
        tvStatus.setText(getStatus(Integer.parseInt(myLoanList.status)));
        String code=myLoanList.borrowCryptoCode;
        String deadLine=!TextUtils.isEmpty(myLoanList.deadline)?TimeUtils.getDateToStringS(Long.parseLong(myLoanList.deadline),TimeUtils.YYYY_MM_dd_HH_MM_SS1):"";
        tvLoanCount.setText(DoubleUtils.doubleTransRound6(myLoanList.borrowAmount)+" "+code);
        tvInterest.setText(DoubleUtils.doubleTransRound6(myLoanList.interest)+" "+code);//利息
        tvDeadLine.setText(deadLine);
        tvTime.setText(TimeUtils.getDateToStringS(Long.parseLong(myLoanList.createTime),TimeUtils.YYYY_MM_dd_HH_MM_SS1));
        layoutOperation.setVisibility("1".equals(myLoanList.status)?View.VISIBLE:View.GONE);
        if(layoutOperation.getVisibility()==View.VISIBLE){//去还款布局
            tvOperation.setOnClickListener(gotoRepayOnClickListener);
            tvOperation.setTag(R.id.object,myLoanList);
        }
        tvCode.setOnClickListener(lookMorelickListener);
        //传数据到详情页
        tvCode.setTag(R.id.position,position);
        tvCode.setTag(R.id.str,myLoanList.id);
        tvCode.setTag(R.id.str1,!TextUtils.isEmpty(myLoanList.deadline)?myLoanList.deadline:"");
        tvCode.setTag(R.id.str2,!TextUtils.isEmpty(myLoanList.quotaFullTime)?TimeUtils.getDateToStringS(Long.parseLong(myLoanList.quotaFullTime),TimeUtils.YYYY_MM_dd_HH_MM_SS1):"");
        tvCode.setTag(R.id.str3,!TextUtils.isEmpty(myLoanList.realPaybackTime)?TimeUtils.getDateToStringS(Long.parseLong(myLoanList.realPaybackTime),TimeUtils.YYYY_MM_dd_HH_MM_SS1):"");
        tvCode.setTag(R.id.str4,getStatus(Integer.parseInt(myLoanList.status)));
        tvCode.setTag(R.id.str5,DoubleUtils.doubleTransRound6(myLoanList.interest)+" "+code);
        return viewHolder.getConvertView();
    }
    //状态   -2：募集失败（逾期）；-1募集失败；0 募集中;1募集成功，待还款；11提前还款；
    // 12正常还款；13逾期还款；14.还款完成；16强制交割；17：强制平仓?
    private String getStatus(int status){
        //1 两个按钮  0隐藏去还款
        if(status==-2){
            return context.getString(R.string.within_the_time_limit);
        }else if(status==-1){
            return context.getString(R.string.raise_failed);
        }else if(status==0){
            return context.getString(R.string.raiseing);
        }else if(status==1){
            return context.getString(R.string.wait_repay);
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
        }else if(status==-1){
            return context.getString(R.string.forced_to_unwind);
        }else{
            return "";
        }
    }

}

package com.lend.lendchain.ui.fragment.rechargewithdraw.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MessageEvent;
import com.lend.lendchain.bean.Recharge;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.helper.RxBus;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.DoubleUtils;
import com.lend.lendchain.utils.TimeUtils;
import com.lend.lendchain.utils.ViewUtils;
import com.yangfan.utils.CountDownTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class RechargeAdapter extends BaseAdapter {
    private Set<Integer> addPos = new LinkedHashSet<>();
    private List<Recharge> list = new ArrayList<>();
    private Map<Integer, CountDownTimer> countDownTimers = new HashMap<>();//保存计时器 对应每个cell
    private Map<Integer, String> countDownTimes = new HashMap<>();//保存计时器的初试时间 对应每个cell
    private Context context;
    private boolean isRefrensh;

    public RechargeAdapter(Context context) {
        this.context = context;
    }

    //点击显示隐藏
    private View.OnClickListener addOnClickListener = v -> {
        int position = (int) v.getTag(R.id.position);
        LinearLayout llAdd = (LinearLayout) v.getTag(R.id.view);
        if (llAdd.getVisibility() == View.VISIBLE) {
            llAdd.setVisibility(View.GONE);
            addPos.remove(position);
        } else {
            llAdd.setVisibility(View.VISIBLE);
            addPos.add(position);
        }
    };
    //点击去支付
    private View.OnClickListener payClickListener = v -> {
        String orderId = (String) v.getTag(R.id.str);
        RxBus.getInstance().post(new MessageEvent<String>(MessageEvent.RECHARGE_BLOCKCITY_GOPAY, orderId));
    };
    //点击关闭订单
    private View.OnClickListener closeClickListener = v -> {
        String orderId = (String) v.getTag(R.id.str);
        RxBus.getInstance().post(new MessageEvent<String>(MessageEvent.RECHARGE_BLOCKCITY_CLOSE, orderId));
    };

    public void loadData(List<Recharge> list) {
        countDownTimers.clear();
        countDownTimes.clear();
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reLoadData(List<Recharge> list) {
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
        Recharge rechargeWithDraw = list.get(position);
        ViewHolder viewHolder = ViewHolder.get(context, convertView, R.layout.item_recharge_record);
        TextView tvStatus = viewHolder.getView(R.id.recharge_record_tvStatus);
        TextView tvChange = viewHolder.getView(R.id.recharge_record_tvChange);
        TextView tvCount = viewHolder.getView(R.id.recharge_record_tvCount);
        TextView tvTime = viewHolder.getView(R.id.recharge_record_tvTime);
        TextView tvOrderCode = viewHolder.getView(R.id.recharge_record_tvOrderCode);
        TextView tvOrderAdd = viewHolder.getView(R.id.recharge_record_tvOrderAdd);
        TextView tvHash = viewHolder.getView(R.id.recharge_record_tvHash);
        TextView tvGoPay = viewHolder.getView(R.id.recharge_record_tvGoPay);
        TextView tvTimeAfter = viewHolder.getView(R.id.recharge_record_tvTimeAfter);
        TextView tvClose = viewHolder.getView(R.id.recharge_record_tvClose);
        LinearLayout llOrderAdd = viewHolder.getView(R.id.recharge_record_llOrderAdd);
        LinearLayout llOrder = viewHolder.getView(R.id.recharge_record_llOrder);
        tvChange.setVisibility(rechargeWithDraw.flag==0?View.GONE:View.VISIBLE);//0不显示 1显示
        String status = rechargeWithDraw.status;
        tvStatus.setText(getRechargeStatus(status));
        tvCount.setText("+" + DoubleUtils.doubleTransRound6(rechargeWithDraw.amount) + " " + rechargeWithDraw.cryptoCode);
        tvTime.setText(TimeUtils.getDateToStringMs(Long.parseLong(rechargeWithDraw.ctime), "yyyy.MM.dd HH:mm:ss"));
        //订单号 充值 orderId 提现 txOrder
        tvOrderCode.setText(context.getString(R.string.order_code) + ":" + rechargeWithDraw.orderId);//订单号
        //地址 充值 addr addr 提现addrTo addr
        String orderAddText = rechargeWithDraw.addr.addr;
        tvOrderAdd.setText(context.getString(R.string.recharge_add) + ":" + orderAddText);//充值地址
        //充值 hash orderId
        tvHash.setText("Hash:"+rechargeWithDraw.orderId);
        llOrder.setOnClickListener(addOnClickListener);
        llOrder.setTag(R.id.position, position);
        llOrder.setTag(R.id.view, llOrderAdd);
        llOrderAdd.setVisibility(addPos.contains(position) ? View.VISIBLE : View.GONE);
        CountDownTimer countDownTimer;
        if ("0".equals(status)) {
            //待支付状态 展示倒计时
            tvGoPay.setTag(R.id.str, rechargeWithDraw.orderId);
            tvGoPay.setOnClickListener(payClickListener);
            tvClose.setOnClickListener(closeClickListener );
            tvClose.setTag(R.id.str, rechargeWithDraw.orderId);
            ViewUtils.showViewsVisible(true, tvGoPay, tvTimeAfter,tvClose);//显示去支付 和 倒计时
            if(!countDownTimes.containsKey(position)){//存储上次的计时
                //计算时间差 开启倒计时
                countDownTimes.put(position,String.valueOf(Long.valueOf(rechargeWithDraw.expireTime)-System.currentTimeMillis()));
            }
            if (countDownTimers.containsKey(position)) {//为了防止countDownTimer new多次计时不准
                countDownTimer = countDownTimers.get(position);
                countDownTimer.cancel();
            }
            if(Long.valueOf(countDownTimes.get(position))>0){
                //初始时间 text默认文案
                SparseIntArray t=TimeUtils.getDistanceTimes(System.currentTimeMillis(),Long.valueOf(rechargeWithDraw.expireTime));
                String text= CommonUtil.preZeroForBall(t.get(2))+context.getString(R.string.time_min)+CommonUtil.preZeroForBall(t.get(3))+context.getString(R.string.time_sec);
                tvTimeAfter.setText(String.format(context.getString(R.string.after_time_failure),text));
            }else{
                tvTimeAfter.setText("");//倒计时 归零后 无显示
            }
            if(Long.valueOf(countDownTimes.get(position))>0){
                countDownTimer = new CountDownTimer(Long.valueOf(countDownTimes.get(position)), 1000) {
                    @Override
                    public void onTick(long l) {
                        //开启倒计时 每次计算时间差 转化为时间
                        countDownTimes.put(position,String.valueOf(Long.valueOf(rechargeWithDraw.expireTime)-System.currentTimeMillis()));
                        SparseIntArray t=TimeUtils.getDistanceTimes(System.currentTimeMillis(),Long.valueOf(rechargeWithDraw.expireTime));
                        String text= CommonUtil.preZeroForBall(t.get(2))+context.getString(R.string.time_min)+CommonUtil.preZeroForBall(t.get(3))+context.getString(R.string.time_sec);
                        tvTimeAfter.setText(String.format(context.getString(R.string.after_time_failure),text));
                    }

                    @Override
                    public void onFinish() {
                        //刷新列表
                        RxBus.getInstance().post(new MessageEvent<String>(MessageEvent.RECHARGE_BLOCKCITY_COUNTDOWN));
                    }
                };
                countDownTimers.put(position, countDownTimer);
                countDownTimer.start();
            }
        } else {
            ViewUtils.showViewsVisible(false, tvGoPay, tvTimeAfter,tvClose);//隐藏去支付 和 倒计时
        }
        return viewHolder.getConvertView();
    }

    //充值类型状态
    private String getRechargeStatus(String status) {
        if ("1".equals(status)) {
            return context.getString(R.string.confirming);
        } else if ("2".equals(status)) {
            return context.getString(R.string.wait_in_account);
        } else if ("3".equals(status)) {
            return context.getString(R.string.recharge_success);
        } else if ("0".equals(status)) {
            return context.getString(R.string.wait_pay);
        } else if ("4".equals(status)) {
            return context.getString(R.string.order_cancle);
        } else {
            return "";
        }
    }

}

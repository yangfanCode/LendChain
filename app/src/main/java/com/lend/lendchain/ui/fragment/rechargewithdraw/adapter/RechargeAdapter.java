package com.lend.lendchain.ui.fragment.rechargewithdraw.adapter;

import android.content.Context;
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

    public void loadData(List<Recharge> list) {
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
        TextView tvCount = viewHolder.getView(R.id.recharge_record_tvCount);
        TextView tvTime = viewHolder.getView(R.id.recharge_record_tvTime);
        TextView tvOrderCode = viewHolder.getView(R.id.recharge_record_tvOrderCode);
        TextView tvOrderAdd = viewHolder.getView(R.id.recharge_record_tvOrderAdd);
        TextView tvHash = viewHolder.getView(R.id.recharge_record_tvHash);
        TextView tvGoPay = viewHolder.getView(R.id.recharge_record_tvGoPay);
        TextView tvTimeAfter = viewHolder.getView(R.id.recharge_record_tvTimeAfter);
        LinearLayout llOrderAdd = viewHolder.getView(R.id.recharge_record_llOrderAdd);
        LinearLayout llOrder = viewHolder.getView(R.id.recharge_record_llOrder);
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
            tvGoPay.setTag(R.id.str, rechargeWithDraw.orderId);
            tvGoPay.setOnClickListener(payClickListener);
            ViewUtils.showViewsVisible(true, tvGoPay, tvTimeAfter);//显示去支付 和 倒计时
            if(!countDownTimes.containsKey(position)){
                countDownTimes.put(position,String.valueOf(rechargeWithDraw.expireTime));
            }
            if (countDownTimers.containsKey(position)) {//为了防止countDownTimer new多次计时不准
                countDownTimer = countDownTimers.get(position);
                countDownTimer.cancel();
            }
            tvTimeAfter.setText(String.format(context.getString(R.string.after_time_failure),TimeUtils.getDateToStringMs(Long.valueOf(countDownTimes.get(position)), TimeUtils.HH_MM_SS)));
            countDownTimer = new CountDownTimer(Long.valueOf(countDownTimes.get(position)), 1000) {
                @Override
                public void onTick(long l) {
                    countDownTimes.put(position,String.valueOf(l));
                    tvTimeAfter.setText(String.format(context.getString(R.string.after_time_failure),TimeUtils.getDateToStringMs(l, TimeUtils.HH_MM_SS)));
                }

                @Override
                public void onFinish() {
                    //刷新列表
                    RxBus.getInstance().post(new MessageEvent<String>(MessageEvent.RECHARGE_BLOCKCITY_COUNTDOWN));
                    this.cancel(); //防止new出多个导致时间跳动加速
                }
            };
            countDownTimers.put(position, countDownTimer);
            countDownTimer.start();
//            countDownTime(Long.valueOf(rechargeWithDraw.expireTime), tvTimeAfter);
        } else {
            ViewUtils.showViewsVisible(false, tvGoPay, tvTimeAfter);//隐藏去支付 和 倒计时
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

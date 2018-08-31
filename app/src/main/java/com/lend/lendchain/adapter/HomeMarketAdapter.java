package com.lend.lendchain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.HomeMarket;
import com.lend.lendchain.bean.MessageEvent;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.helper.RxBus;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.widget.chart.utils.NumberUtil;
import com.lend.lendchain.widget.chart.view.HomeMarketFenshiView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class HomeMarketAdapter extends BaseAdapter {
    private double rateRMB2Dollar;
    private List<HomeMarket>list=new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private int showPos=-1;//展示k线pos
    private View.OnClickListener onClickListener=v -> {
        int position= (int) v.getTag(R.id.position);
        LinearLayout layout24h= (LinearLayout) v.getTag(R.id.view);
        HomeMarketFenshiView fenshiView= (HomeMarketFenshiView) v.getTag(R.id.view1);
        if(position==showPos&&layout24h.getVisibility()==View.VISIBLE){
            showPos=-1;
        }else{
            showPos=position;
        }
        RxBus.getInstance().post(new MessageEvent<HomeMarketFenshiView>(MessageEvent.HOME_MARKEY_KLINE,list.get(position).pairId,fenshiView));
    };
    public HomeMarketAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void loadData(List<HomeMarket>list,double rateRMB2Dollar){
        this.list.clear();
        this.list.addAll(list);
        this.rateRMB2Dollar=rateRMB2Dollar;
        notifyDataSetChanged();
    }

    public void setShowPos(int showPos){
        this.showPos=showPos;
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
        HomeMarket homeMarket=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_home_market);
        TextView tvPairName=viewHolder.getView(R.id.item_home_market_tvPairName);
        TextView tvNewPrice=viewHolder.getView(R.id.item_home_market_tvNewPrice);
        TextView tvNewPriceRMB=viewHolder.getView(R.id.item_home_market_tvNewPriceRMB);
        TextView tvNewGain=viewHolder.getView(R.id.item_home_market_tvNewGain);
        LinearLayout layout=viewHolder.getView(R.id.item_home_market_layout);
        LinearLayout line24hLayout=viewHolder.getView(R.id.item_home_market_24hLayout);
        TextView tvHighest24h=viewHolder.getView(R.id.item_home_market_tvHighest24h);//24h最高
        TextView tvLowest24h=viewHolder.getView(R.id.item_home_market_tvLowest24h);//24h最低
        HomeMarketFenshiView fenshiView=viewHolder.getView(R.id.item_home_market_fenshiView);//24h分时图
        tvPairName.setText(homeMarket.pairCode);//交易对名字
        tvNewPrice.setText(CommonUtil.doubleTransRoundTwo(homeMarket.close,4));//美元
        tvNewPriceRMB.setText(context.getString(R.string.RMB).concat(CommonUtil.doubleTransRoundTwo(homeMarket.close*rateRMB2Dollar,4)));//RMB
        tvNewGain.setText(NumberUtil.calcGain(homeMarket.close,homeMarket.open));//涨跌幅
        tvNewGain.setTextColor(ColorUtils.getTextColorAsh(homeMarket.close,homeMarket.open));//涨跌幅颜色
        tvHighest24h.setText(CommonUtil.doubleTransRoundTwo(homeMarket.high,2));//24h最高
        tvLowest24h.setText(CommonUtil.doubleTransRoundTwo(homeMarket.low,2));//24h最高
        layout.setOnClickListener(onClickListener);//点击事件
        layout.setTag(R.id.view,line24hLayout);
        layout.setTag(R.id.position,position);
        layout.setTag(R.id.view1,fenshiView);
        line24hLayout.setVisibility(position==showPos?View.VISIBLE:View.GONE);//默认的显示隐藏
        return viewHolder.getConvertView();
    }

}

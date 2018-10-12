package com.lend.lendchain.ui.activity.account.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lend.lendchain.R;
import com.lend.lendchain.bean.MessageList;
import com.lend.lendchain.bean.ViewHolder;
import com.lend.lendchain.ui.activity.account.MyMessageDetailActivity;
import com.lend.lendchain.utils.ColorUtils;
import com.lend.lendchain.utils.CommonUtil;
import com.lend.lendchain.utils.Constant;
import com.lend.lendchain.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class MyMessageAdapter extends BaseAdapter {
    private List<MessageList.Item> list = new ArrayList();
    private Context context;
    private int type;
    private View.OnClickListener onClickListener=v -> {
        String id= (String) v.getTag(R.id.id);
        String title= (String) v.getTag(R.id.str);
        Bundle bundle=new Bundle();
        bundle.putString(Constant.INTENT_EXTRA_DATA,id);
        bundle.putString(Constant.ARGS_PARAM1,title);
        //跳转详情
        CommonUtil.openActicity(context, MyMessageDetailActivity.class,bundle);
    };
    public MyMessageAdapter(Context context){
        this.context=context;
    }

    public void loadData(List<MessageList.Item> list,int type) {
        this.list.clear();
        this.list.addAll(list);
        this.type=type;
        notifyDataSetChanged();
    }

    public void reLoadData(List<MessageList.Item> list,int type) {
        this.list.addAll(list);
        this.type=type;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
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
        MessageList.Item item=list.get(position);
        ViewHolder viewHolder=ViewHolder.get(context,convertView, R.layout.item_my_message);
        TextView tvTime=viewHolder.getView(R.id.item_my_message_tvTime);
        TextView tvTitle=viewHolder.getView(R.id.item_my_message_tvTitle);
        TextView tvDetail=viewHolder.getView(R.id.item_my_message_tvDetail);
        TextView tvMessage=viewHolder.getView(R.id.item_my_message_tvMessage);
        tvTime.setText(TimeUtils.getDateToStringMs(item.ctime,TimeUtils.YYYY_MM_dd_HH_MM_SS));//时间
        tvTitle.setText(item.title);//标题
        tvMessage.setText(item.content);
        tvDetail.setTextColor(type==1? ColorUtils.COLOR_509FFF:ColorUtils.COLOR_CCCCCC);
        tvDetail.setTag(R.id.id,item.id);
        tvDetail.setTag(R.id.str,item.title);
        tvDetail.setOnClickListener(onClickListener);
        return viewHolder.getConvertView();
    }
}

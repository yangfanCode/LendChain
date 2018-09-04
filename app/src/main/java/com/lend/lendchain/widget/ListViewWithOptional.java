package com.lend.lendchain.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lend.lendchain.R;

/**
 * 统一设置emptyView空数据 未登录 404 500等页面 配合optionallayout
 * Created by yangfan
 * nrainyseason@163.com
 */
public class ListViewWithOptional extends ListView {
    public ListViewWithOptional(Context context) {
        super(context);
    }

    public ListViewWithOptional(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //设置emptyView
    public void setEmptyView(OptionalLayout.TypeEnum typeEnum) {
        OptionalLayout optionalLayout = (OptionalLayout) LayoutInflater.from(getContext()).inflate(R.layout.no_data_view, null);
        optionalLayout.setTypeEnum(typeEnum);
        ViewGroup parent = ((ViewGroup) getParent());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        optionalLayout.setLayoutParams(params);
        parent.addView(optionalLayout);
        setEmptyView(optionalLayout);
    }
}

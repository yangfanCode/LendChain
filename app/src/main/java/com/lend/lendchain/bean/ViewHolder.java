package com.lend.lendchain.bean;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class ViewHolder {
    private final SparseArray<View> sparseVies;
    private View convertView;

    private ViewHolder(Context context, int itemResourceId) {
        sparseVies = new SparseArray<View>();
        convertView = LayoutInflater.from(context).inflate(itemResourceId, null);
        convertView.setTag(this);
    }

    private ViewHolder(Context context, int itemResourceId, ViewGroup viewGroup) {
        sparseVies = new SparseArray<View>();
        convertView = LayoutInflater.from(context).inflate(itemResourceId, viewGroup);
        convertView.setTag(this);
    }


    public static ViewHolder get(Context context, View convertView, int itemResourceId) {
        if (convertView == null) {
            return new ViewHolder(context, itemResourceId);
        }
        return (ViewHolder) convertView.getTag();
    }

    public static ViewHolder get(Context context, View convertView, int itemResourceId, ViewGroup vierGroup) {
        if (convertView == null) {
            return new ViewHolder(context, itemResourceId,vierGroup);
        }
        return (ViewHolder) convertView.getTag();
    }


    public <T extends View> T getView(int viewId) {
        View view = sparseVies.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            sparseVies.put(viewId, view);
        }
        return (T) view;
    }

    public <T extends View> T findViewById(int viewId) {
        return getView(viewId);
    }

    public View getConvertView() {
        return convertView;
    }

}

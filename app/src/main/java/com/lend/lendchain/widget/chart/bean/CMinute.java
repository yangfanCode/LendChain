package com.lend.lendchain.widget.chart.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分时所需要的 数据字段
 */
public class CMinute implements Cloneable, Serializable, Parcelable {

    //时间 单位 秒
    public long time;
    //最新价
    public double price;
    //净 交易量
    public double count;
    //总交易量
    public double amountVol;
    //均价
    public double average;
    //涨跌幅
    public double rate;
    //总交易额
    public double total;

    public double apiVolum;//本地自定义 参数，用于存放 服务端接口 返回的 总交易量，分时图 定时刷新时要用到

    public long getTime() {
        return time;
    }

    public String getTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.format(new Date(time));
        } catch (Exception e) {
            return "--:--";
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setAmountVol(double amountVol) {
        this.amountVol = amountVol;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAmountVol() {
        return amountVol;
    }

    public double getTotal() {
        return total;
    }

    public double getApiVolum() {
        return apiVolum;
    }

    public void setApiVolum(double apiVolum) {
        this.apiVolum = apiVolum;
    }

    public Object clone() {
        CMinute o = null;
        try {
            o = (CMinute) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.time);
        dest.writeDouble(this.price);
        dest.writeDouble(this.count);
        dest.writeDouble(this.average);
        dest.writeDouble(this.rate);
        dest.writeDouble(this.amountVol);
        dest.writeDouble(this.total);
        dest.writeDouble(this.apiVolum);
    }

    public CMinute() {
    }

    protected CMinute(Parcel in) {
        this.time = in.readLong();
        this.price = in.readDouble();
        this.count = in.readDouble();
        this.average = in.readDouble();
        this.rate = in.readDouble();
        this.amountVol = in.readDouble();
        this.total = in.readDouble();
        this.apiVolum = in.readDouble();
    }

    public static final Creator<CMinute> CREATOR = new Creator<CMinute>() {
        @Override
        public CMinute createFromParcel(Parcel source) {
            return new CMinute(source);
        }

        @Override
        public CMinute[] newArray(int size) {
            return new CMinute[size];
        }
    };
}

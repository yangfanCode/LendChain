package com.lend.lendchain.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 已购记录
 */
public class InvestRecordList implements Parcelable {
    public double amount;
    public String email;
    public String id;
    public String nickname;
    public long time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(nickname);
        dest.writeLong(time);
    }

    public static final Parcelable.Creator<InvestRecordList> CREATOR=new Parcelable.Creator<InvestRecordList>() {

        @Override
        public InvestRecordList createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            InvestRecordList investRecordList=new InvestRecordList();
            investRecordList.amount=source.readDouble();
            investRecordList.email=source.readString();
            investRecordList.id=source.readString();
            investRecordList.nickname=source.readString();
            investRecordList.time=source.readLong();
            return investRecordList;
        }

        @Override
        public InvestRecordList[] newArray(int size) {
            // TODO Auto-generated method stub
            return new InvestRecordList[size];
        }
    };
}

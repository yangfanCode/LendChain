package com.lend.lendchain.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 已购记录
 */
public class AddRecordList implements Parcelable {
    public double amount;
    public double preAmount;
    public String email;
    public String id;
    public String nickname;
    public long ctime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeDouble(preAmount);
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(nickname);
        dest.writeLong(ctime);
    }

    public static final Creator<AddRecordList> CREATOR=new Creator<AddRecordList>() {

        @Override
        public AddRecordList createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            AddRecordList investRecordList=new AddRecordList();
            investRecordList.amount=source.readDouble();
            investRecordList.preAmount=source.readDouble();
            investRecordList.email=source.readString();
            investRecordList.id=source.readString();
            investRecordList.nickname=source.readString();
            investRecordList.ctime=source.readLong();
            return investRecordList;
        }

        @Override
        public AddRecordList[] newArray(int size) {
            // TODO Auto-generated method stub
            return new AddRecordList[size];
        }
    };
}

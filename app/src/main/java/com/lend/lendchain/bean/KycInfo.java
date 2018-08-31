package com.lend.lendchain.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * kyc各个资料
 */
public class KycInfo implements Parcelable {
    public String country;
    public String countryCode;
    public String realName;
    public String bornDate;
    public String IdCard;
    public String type;
    public String documentNum;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(countryCode);
        dest.writeString(realName);
        dest.writeString(bornDate);
        dest.writeString(IdCard);
        dest.writeString(type);
        dest.writeString(documentNum);
    }

    public static final Parcelable.Creator<KycInfo> CREATOR=new Parcelable.Creator<KycInfo>() {

        @Override
        public KycInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            KycInfo kycInfo=new KycInfo();
            kycInfo.country=source.readString();
            kycInfo.countryCode=source.readString();
            kycInfo.realName=source.readString();
            kycInfo.bornDate=source.readString();
            kycInfo.IdCard=source.readString();
            kycInfo.type=source.readString();
            kycInfo.documentNum=source.readString();
            return kycInfo;
        }

        @Override
        public KycInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new KycInfo[size];
        }
    };
}

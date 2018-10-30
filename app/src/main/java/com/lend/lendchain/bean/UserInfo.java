package com.lend.lendchain.bean;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 用户信息
 */
public class UserInfo {
    public String country_code;
    public double totalAmount;
    public double noticeAmount;
    public String phone;
    public String nickname;
    public Identif identif;
    public double lastAmount;
    public double profit;
    public String email;

    public static class Identif{
        public int phone;
        public int google;
        public int blockKycStatus;
        public int kyc;
    }
}

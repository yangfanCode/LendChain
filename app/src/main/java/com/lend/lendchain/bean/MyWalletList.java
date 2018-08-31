package com.lend.lendchain.bean;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 我的钱包资产列表
 */
public class MyWalletList {
    public String cryptoCode;
    public String cryptoId;
    public int depositStatus;
    public int withdrawStatus;
    public String id;
    public double amount;
    public double withdrawRate;
    public int transferStatus;
    public DepositAddrs depositAddrs;

    public static class DepositAddrs{
        public String memo;
        public String addr;
    }
}

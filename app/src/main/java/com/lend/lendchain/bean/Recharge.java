package com.lend.lendchain.bean;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class Recharge {
    public int flag;
    public String id;
    public String ctime;
    public String status;
    public String txOrder;//提现订单号
    public double amount;
    public String cryptoCode;
    public String cryptoId;
    public String orderId;//充值订单号 hash
    public String addrTo;//提现地址
    public Addr addr;//充值地址
    public String expireTime;

    public static class AddrTo{
        public String memo;
        public String addr;
    }
    public static class Addr{
        public String memo;
        public String addr;
    }
}

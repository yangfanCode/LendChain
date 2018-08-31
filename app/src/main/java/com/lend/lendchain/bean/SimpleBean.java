package com.lend.lendchain.bean;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 通用简单model
 */
public class SimpleBean {
    //谷歌验证码
    public String secret;
    public String email;
    //获取某一币种数量
    public double amount;
    public String cryptoCode;
    public String cryptoId;
    public double freezeAmount;
    public double totalAmount;
    public double lastNewAmount;
    //借款类型
    public String id;
    public String name;
    //获取币种对价格
    public String symbol;
    public double price;
    public long timestamp;
    //获取币的属性
    public double minWithdraw;
    public double minDeposit;
    public double withdrawFee;
    public double dayWithdraw;
    public double minBorrowAmount;
}

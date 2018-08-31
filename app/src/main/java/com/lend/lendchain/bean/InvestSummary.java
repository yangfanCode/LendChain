package com.lend.lendchain.bean;

import java.util.ArrayList;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * 投资详情
 */
public class InvestSummary {
    public double borrowAmount;
    public String borrowCryptoCode;
    public int borrowCryptoId;
    public int borrowDays;
    public double boughtAmount;
    public String boughtPercent;
    public long createTime;
    public String id;
    public String desc;
    public double interestRates;
    public ArrayList<InvestRecordList> investList;
    public ArrayList<AddRecordList> mortgateAddList;
    public double mortgageAmount;
    public double minInvestAmount;
    public double minAmount;
    public String mortgageCryptoCode;
    public int mortgageCryptoId;
    public double mortgagePrice;
    public String nickname;
    public String orderId;
    public int status;
    public int userId;
    public int borrowTypeId;
    public long startTime;
}

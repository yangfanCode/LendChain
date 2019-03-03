package com.lend.lendchain.bean;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class ChangeList {
    public int total;
    public int size;
    public int pages;
    public int current;
    public List<Detail>records;
    public static class Detail{
        public String id;
        public String depositCoinCode;
        public double depositAmount;
        public double realReceiveAmount;
        public String receiveCoinCode;
        public int status;
        public String exchangeTime;
    }
}

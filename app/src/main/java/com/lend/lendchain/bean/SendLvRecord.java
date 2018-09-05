package com.lend.lendchain.bean;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class SendLvRecord {
    public int total;
    public int pages;
    public int current;
    public List<Detail>records;
    public static class Detail{
        public String id;
        public double amount;
        public String time;
        public int type;
        public String cryptoCode;
        public int cryptoId;
        public String reason;
        public String remark;
    }
}

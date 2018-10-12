package com.lend.lendchain.bean;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class MessageList {
    public List<Item> records;
    public static class Item{
        public String id;
        public String title;
        public long ctime;
        public String content;
    }
}

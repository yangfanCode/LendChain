package com.lend.lendchain.bean;

/**
 * Created by yangfan on 2018/1/23.
 */

public class MessageEvent<T> {
    public String message;
    public T data;
    public int type;
    public int position;
    public static final int HOME_MARKEY_KLINE = 1;//首页行情K线

    public MessageEvent(String message) {
        this.message = message;
    }

    public MessageEvent(int type) {
        this.type = type;
    }

    public MessageEvent(String message, T data) {
        this.message = message;
        this.data = data;
    }
    public MessageEvent(int type, T data) {
        this.type=type;
        this.data = data;
    }
    public MessageEvent(int type,int position, T data) {
        this.type=type;
        this.position=position;
        this.data = data;
    }
}

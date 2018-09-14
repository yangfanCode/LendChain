package com.lend.lendchain.enums;

import com.lend.lendchain.R;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public enum CoinEnum {
    BTC("BTC",R.mipmap.btc),
    ETH("ETH",R.mipmap.eth),
    GXS("GXS",R.mipmap.gxs),
    USDT("USDT",R.mipmap.usdt),
    LV("LV",R.mipmap.lv),
    QKC("QKC",R.mipmap.qkc),
    DACC("DACC",R.mipmap.dacc);

    private String coinName;
    private int coinIcon;

    public int getCoinIcon(){
        return coinIcon;
    }
    public String getCoinName(){
        return coinName;
    }

    CoinEnum(String coinName,int coinIcon){
        this.coinName=coinName;
        this.coinIcon=coinIcon;
    }

    public static CoinEnum createWithCoinEnum(String coinName) {
        if (coinName == null) return null;
        for (CoinEnum coinEnum : CoinEnum.values()) {
            if (coinEnum.coinName.equals(coinName)) {
                return coinEnum;
            }
        }
        return null;
    }

}

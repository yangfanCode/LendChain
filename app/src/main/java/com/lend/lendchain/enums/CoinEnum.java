package com.lend.lendchain.enums;

import com.lend.lendchain.R;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public enum CoinEnum {
    BTC("BTC", R.mipmap.btc, R.mipmap.btc_border),
    ETH("ETH", R.mipmap.eth, R.mipmap.eth_border),
    GXS("GXS", R.mipmap.gxs, R.mipmap.gxs_border),
    USDT("USDT", R.mipmap.usdt, R.mipmap.usdt),
    LV("LV", R.mipmap.lv, R.mipmap.lv),
    QKC("QKC", R.mipmap.qkc, R.mipmap.qkc_border),
    DACC("DACC", R.mipmap.dacc, R.mipmap.dacc_border);

    private String coinName;
    private int coinIcon;
    private int coinIconBorder;

    public int getCoinIcon() {
        return coinIcon;
    }

    public String getCoinName() {
        return coinName;
    }

    public int getCoinIconBorder() {
        return coinIconBorder;
    }

    CoinEnum(String coinName, int coinIcon, int coinIconBorder) {
        this.coinName = coinName;
        this.coinIcon = coinIcon;
        this.coinIconBorder = coinIconBorder;
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

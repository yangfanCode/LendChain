package com.lend.lendchain.utils;

import com.lend.lendchain.bean.CoinIconList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfan
 * nrainyseason@163.com
 * APP 币种 图片单例工具类
 */
public class CoinIconUtils {
    private Map<String, String> icon = new HashMap<>();
    private static CoinIconUtils instance;
    private CoinIconUtils() { }
    public static CoinIconUtils getInstance() {
        if (instance == null) {
            instance = new CoinIconUtils();
        }
        return instance;
    }



    //初始化数据
    public void setIcons(List<CoinIconList> list) {
        if (list == null) return;
        icon.clear();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i).code;
            String value = list.get(i).icon;
            icon.put(key, value);
        }
    }

    //获取图片地址
    public String getIcon(String code) {
        if (icon.containsKey(code)) {
            return icon.get(code);
        }
        return "";
    }
}

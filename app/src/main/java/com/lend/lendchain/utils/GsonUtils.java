package com.lend.lendchain.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */
public class GsonUtils {
    /**
     * 将json字符串解析为map
     *
     * @param json
     * @return
     */
    public static Map<Object, Object> getMapFromJsonStr(String json) {
        Map<Object, Object> map = new Gson().fromJson(json, new TypeToken<Map<Object, Object>>() {
        }.getType());
        return map;
    }
    /**
     * 将json字符串解析为List
     *
     * @param json
     * @return
     */
    public static List<Object> getListFromJsonStr(String json) {
        List<Object> list = new Gson().fromJson(json, new TypeToken<List<Object>>() {
        }.getType());
        return list;
    }
}

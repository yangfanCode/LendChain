package com.lend.lendchain.network;


import com.lend.lendchain.enums.AppEnvEnum;
import com.lend.lendchain.enums.AppEnvHelper;

/**
 * Created by yangfan on 2016/11/29.
 */
public class NetConst {
    /**
     * 此处为线上域名
     **/
    public static final String PRODUCT_HOST = "https://api.lendx.vip/";//线上主域名
    public static final String PRODUCT_HOST_H5="https://info.lendx.vip/"; //H5线上主域名


    /**
     * 此处为测试域名
     **/
    public static final String PRODUCT_HOST_TEST = "http://apitest.lendx.vip/";//debug主域名
    public static final String PRODUCT_HOST_H5_TEST="https://info.lendx.vip/"; //H5debug主域名




    /**以下为h5子页面地址**/
    //banner
    public static final String BANNER="https://lendchain.oss-cn-shanghai.aliyuncs.com/mobile/index.json";
    //关于我们
    public static final String ABOUT_US="app/about/about.html?lang=";
    //费率说明
    public static final String RATE_TIPNS="app/Helpdetail/Helpdetail.html?item=Rate&lang=";
    //风险管理
    public static final String RIST_MANAGE="app/Helpdetail/Helpdetail.html?item=risk&lang=";
    //新手指南
    public static final String NEWBIE_GUIDE="app/Helpdetail/Helpdetail.html?item=Helpdetail&lang=";
    //如何玩转抵押借贷
    public static final String HOW_TO_PLAY_MORGTAGE="app/Helpdetail/Helpdetail.html?item=usemortgage&lang=";
    //抵押借款说明
    public static final String MORTGAGE_DESCRIPTION="app/Helpdetail/Helpdetail.html?item=mortgage&lang=";
    //推荐有礼
    public static final String SUPPORT_GIFT="app/recommendact/recommendact.html?lang=";


    //主域名
    public static String dynamicBaseUrl() {
        if (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE) {
            return PRODUCT_HOST;
        }
        return PRODUCT_HOST_TEST;
    }

    //H5域名
    public static String dynamicBaseUrlForH5() {
        if (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE) {
            return PRODUCT_HOST_H5;
        }
        return PRODUCT_HOST_H5_TEST;
    }

}

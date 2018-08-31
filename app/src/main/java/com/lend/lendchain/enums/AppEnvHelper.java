package com.lend.lendchain.enums;


import com.lend.lendchain.BuildConfig;

/**
 * Created by yangfan on 2017/07/10.
 */

public class AppEnvHelper {

    public static AppEnvEnum currentEnv() {

        if (BuildConfig.BUILD_TYPE.equals("release")) {
            return AppEnvEnum.ONLINE;
        }

        return AppEnvEnum.DEBUG;
    }

}

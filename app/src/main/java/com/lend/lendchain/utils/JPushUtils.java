package com.lend.lendchain.utils;


import com.lend.lendchain.enums.AppEnvEnum;
import com.lend.lendchain.enums.AppEnvHelper;

/**
 * 极光推送utils
 * Created by yangfan
 * nrainyseason@163.com
 */

public class JPushUtils {
    /**
     * 极光 alias
     * boolean isSetAlias  ; true 设置 alias，false 清空
     */
    public static void jPushAlias(boolean isSetAlias, String alias) {
//        LogUtils.LogE(jPushAlias.class, "alias =" + alias);
//        TagAliasBean tagAliasBean = new TagAliasBean();
//        if (isSetAlias)
//            tagAliasBean.action = ACTION_SET;
//        else
//            tagAliasBean.action = ACTION_DELETE;
//        sequence++;
//        tagAliasBean.alias = alias;
//
//        tagAliasBean.isAliasAction = true;
//        TagAliasOperatorHelper.getInstance().handleAction(MyApplicaion.getContext().getApplicationContext(), sequence, tagAliasBean);

    }

    public static String getHxImUserIdByAppUserId(String userId) {
//        return userId;
        return (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE ? "" : "test") + userId;
    }

    public static String getAppUserIdByHxImUserId(String hxImUserId) {
        if (hxImUserId == null) return "";
//        return hxImUserId;
        return AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE ? hxImUserId : (hxImUserId.startsWith("test") ? hxImUserId.substring(4) : hxImUserId);
    }
}

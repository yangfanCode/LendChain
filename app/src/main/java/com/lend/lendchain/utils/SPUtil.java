package com.lend.lendchain.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by js on 2017/1/9.
 */
public class SPUtil {
    /**
     * user_id
     */
    public static final String KEY_LOGIN_USER_ID = "key_login_user_id";
    /**
     * user_pwd
     */
    public static final String KEY_LOGIN_USER_PWD = "key_login_user_pwd";
    /**
     * user_name
     */
    public static final String KEY_LOGIN_USER_NAME = "key_login_user_name";
    /**
     * 登录状态
     */
    public static final String KEY_LOGIN_STATE = "key_login_state";
    /**
     * 登录 userinfo
     */
    public static final String KEY_LOGIN_USER_INFO = "key_login_user_info";
    /**
     * 登录 token
     */
    public static final String KEY_LOGIN_USER_TOKEN = "key_login_user_token";
    /**
     * 登录 邮箱
     */
    public static final String KEY_LOGIN_USER_EMAIL = "key_login_user_email";
    /**
     * 版本升级
     */
    public static final String KEY_VERSIONID = "key_version_id";
    /**********************认证相关******************************/
    /**
     * 手机认证
     */
    public static final String KEY_LOGIN_USER_PHONE = "key_login_user_phone";
    /**
     * 谷歌认证
     */
    public static final String KEY_LOGIN_USER_GOOGLE = "key_login_user_google";
    /**
     * kyc认证
     */
    public static final String KEY_LOGIN_USER_KYC = "key_login_user_kyc";
    /**
     * 用户设置的显示隐藏总资产
     *
     */
    public static final String KEY_LOGIN_USER_TOALOAMOUNT = "key_login_user_totalamount";
    /**
     * 多语言设置
     */
    public static final String KEY_LANGUAGE_SETTING="key_language_setting";
    private static Application context;
    public static void initWithApplication(Application application) {
        context = application;
    }

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "LendChain";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        put(context, key, object);
    }

    private static void put(Context context, String key, Object object)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        } else
        {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    private static Object get(Context context, String key, Object defaultObject)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        } else if (defaultObject instanceof Double)
        {
            String val = sp.getString(key, String.valueOf(defaultObject));
            return Double.parseDouble(val);
        } else {
            return sp.getString(key, (String) defaultObject);
        }
    }

    /**
     * sp中读取string字段
     * @param key
     * @return
     */
    public static String getString(String key) {
        String ret = (String) get(context, key ,null);
        return ret;
    }

    /**
     * sp中读取string字段
     * @param key
     * @param def
     * @return
     */
    public static String getString(String key, String def) {
        String ret = (String) get(context, key ,def);
        return ret;
    }

    /**
     * sp中读取int字段
     * @param key
     * @return
     */
    public static int getInt(String key) {
        Integer def = 0;
        Integer ret = (Integer) get(context, key ,def);
        return ret;
    }

    public static int getAndIncrease(String key) {
        Integer def = 0;
        Integer ret = (Integer) get(context, key ,def);
        put(key, ret + 1);
        return ret;
    }

    /**
     * sp中读取int字段
     * @param key
     * @return
     */
    public static int getInt(String key, int def) {
        return (Integer) get(context, key ,def);
    }

    /**
     * sp中读取bool字段
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        Boolean def = false;
        Boolean ret = (Boolean) get(context, key ,def);
        return ret;
    }

    /**
     * sp中读取float字段
     * @param key
     * @return
     */
    public static float getFloat(String key) {
        Float def = 0f;
        Float ret = (Float) get(context, key ,def);
        return ret;
    }

    /**
     * sp中读取long字段
     * @param key
     * @return
     */
    public static long getLong(String key) {
        Long def = 0l;
        Long ret = (Long) get(context, key ,def);
        return ret;
    }

    /**
     * sp中读取double字段
     * @param key
     * @return
     */
    public static double getDouble(String key) {
        Double def = 0d;
        Double ret = (Double) get(context, key ,def);
        return ret;
    }

    /**
     * 移除某个key值已经对应的值
     * @param key
     */
    public static void remove(String key) {
        remove(context, key);
    }

    /**
     * 移除某个key值已经对应的值
     * @param context
     * @param key
     */
    public static void remove(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     * @param context
     */
    public static void clear(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 是否包含key
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return contains(context, key);
    }

    /**
     * 查询某个key是否已经存在
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     *
     */
    private static class SharedPreferencesCompat
    {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static Method findApplyMethod()
        {
            try
            {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e)
            {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor)
        {
            try
            {
                if (sApplyMethod != null)
                {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e)
            {
            } catch (IllegalAccessException e)
            {
            } catch (InvocationTargetException e)
            {
            }
            editor.commit();
        }
    }

    /**
     * 设置退出登录状态 ,清除所有登录信息
     */
    public static void setLoginExit(){
        SPUtil.remove(SPUtil.KEY_LOGIN_USER_TOKEN);
        SPUtil.remove(SPUtil.KEY_LOGIN_USER_NAME);
        SPUtil.remove(SPUtil.KEY_LOGIN_USER_PWD);
        SPUtil.remove(SPUtil.KEY_LOGIN_STATE);
    }
    /**
     * 判断是否登录
     * @return
     */
    public static boolean isLogin() {
        return getBoolean(KEY_LOGIN_STATE);
    }

    /**
     * 设置登录状态
     * @param isLogin
     */
    public static void setLoginState(boolean isLogin){
        put(KEY_LOGIN_STATE,isLogin);
    }

    /**
     * 获得userId
     * @return
     */
    public static int getUserId(){
        return getInt(KEY_LOGIN_USER_ID);
    }

    /**
     * 设置userId
     * @param userId
     */
    public static void setUserId(int userId){
        put(KEY_LOGIN_USER_ID,userId);
    }
    /**
     * 获得token
     * @return
     */
    public static String getToken(){
        return getString(KEY_LOGIN_USER_TOKEN);
    }

    /**
     * 设置token
     * @param token
     */
    public static void setToken(String token){
        put(KEY_LOGIN_USER_TOKEN,token);
    }
    /**
     * 获得邮箱
     * @return
     */
    public static String getEmail(){
        return getString(KEY_LOGIN_USER_EMAIL);
    }

    /**
     * 设置邮箱
     * @param email
     */
    public static void setEmail(String email){
        put(KEY_LOGIN_USER_EMAIL,email);
    }

//    /**
//     * 设置用户名
//     * @param token
//     */
//    public static void setUserName(String token){
//        put(KEY_LOGIN_USER_NAME,token);
//    }
//    /**
//     * 获得用户名
//     * @return
//     */
//    public static String getUserName(){
//        return getString(KEY_LOGIN_USER_NAME);
//    }
//
//    /**
//     * 设置密码
//     * @param token
//     */
//    public static void setUserPwd(String token){
//        put(KEY_LOGIN_USER_PWD,token);
//    }
//    /**
//     * 获得密码
//     * @return
//     */
//    public static String getUserPwd(){
//        return getString(KEY_LOGIN_USER_PWD);
//    }

    /******************认证相关***********************************/

    /**
     * 设置手机认证
     * @param sign
     * 1 认证 0未认证
     */
    public static void setUserPhone(int sign){
        put(KEY_LOGIN_USER_PHONE,sign==1);
    }
    /**
     * 获得手机认证
     * @return
     */
    public static boolean getUserPhone(){
        return getBoolean(KEY_LOGIN_USER_PHONE);
    }
    /**
     * 设置谷歌认证
     * @param sign
     */
    public static void setUserGoogle(int sign){
        put(KEY_LOGIN_USER_GOOGLE,sign==1);
    }
    /**
     * 获得谷歌认证
     * @return
     */
    public static boolean getUserGoogle(){
        return getBoolean(KEY_LOGIN_USER_GOOGLE);
    }
    /**
     * 设置kyc认证
     * @param sign
     */
    public static void setUserKyc(int sign){
        put(KEY_LOGIN_USER_KYC,sign);
    }
    /**
     * 获得kyc认证
     * @return
     */
    public static int getUserKyc(){
        return getInt(KEY_LOGIN_USER_KYC);
    }
    /**
     * 设置总资产是否隐藏
     * @param sign
     */
    public static void setUserTotalAmountGone(boolean sign){
        put(KEY_LOGIN_USER_TOALOAMOUNT,sign);
    }
    /**
     * 获得总资产是否隐藏
     * @return
     */
    public static boolean getUserTotalAmountGone(){
        return getBoolean(KEY_LOGIN_USER_TOALOAMOUNT);
    }

}

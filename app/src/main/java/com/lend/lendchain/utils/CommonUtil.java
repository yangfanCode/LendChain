package com.lend.lendchain.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.lend.lendchain.helper.ContextHelper;
import com.lend.lendchain.ui.activity.login.LoginActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class CommonUtil {
    /**
     * 接口头参数
     * String 类型  否则报错
     */
    public static Map getHeaderParamsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("Authorization", "Basic ZnJvbnRlbmQ6ZnJvbnRlbmQ=");//messageId
        return map;
    }

    /**
     * 接口传json map  里面存放header + body
     * String 类型  否则报错
     */
    public static Map getServiceParamsMap(Map<String, Object> header, Map<String, Object> body) {
        Map<String, Object> map = new HashMap<>();
        if (header != null) map.put("header", header);
        if (body != null) map.put("body", body);
        return map;
    }

    /**
     * 获得 RequestBody
     *
     * @param transactionType
     * @param body
     * @return
     */
    public static RequestBody getRequestBody(String transactionType, Map<String, Object> body) {
        String json = new Gson().toJson(CommonUtil.getServiceParamsMap(CommonUtil.getHeaderParamsMap(), body));//要传递的json
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        return requestBody;
    }

    /**
     * 获取手机的唯一标识
     * IMEI（需要READ_PHONE_STATE权限）、Android ID（在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c  或者恢复出产设置或重装系统会变）、WLAN MAX（无wifi获取有问题还有android6.0小米某版本有问题）等
     * mac地址: android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。
     * //        String macAddress= "";
     * //        WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
     * //        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
     * //        macAddress = wifiInfo.getMacAddress();
     */
    public static String getPhoneSign() {
        StringBuilder deviceId = new StringBuilder();
        try {
//            //IMEI（imei）
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = tm.getDeviceId();
//            if (!TextUtils.isEmpty(imei)) {
////                deviceId.append("imei");
//                deviceId.append(imei);
//
//                LogUtil.LogE(CommonUtil2.class, "getPhoneSign ---> " + deviceId.toString());
//                return deviceId.toString();
//            }
//            //序列号（sn）
//            String sn = tm.getSimSerialNumber();
//            if (!TextUtils.isEmpty(sn) && !sn.equals("9774d56d682e549c")) {
//                deviceId.append("sn");
//                deviceId.append(sn);
//                LogUtil.LogE(CommonUtil2.class, "getPhoneSign ---> " + deviceId.toString());
//                return deviceId.toString();
//            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = SPUtil.getString("uuid", "");
            if (TextUtils.isEmpty(uuid)) {
                uuid = CommonUtil.getUUID().replaceAll("-", "");
                SPUtil.put("uuid", uuid);
            }
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append(uuid.replaceAll("-", ""));
                LogUtils.LogE(CommonUtil.class, "getPhoneSign ---> " + deviceId.toString());
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String uuid = CommonUtil.getUUID().replaceAll("-", "");
            SPUtil.put("uuid", uuid);
            deviceId.append(uuid);
            LogUtils.LogE(CommonUtil.class, "getPhoneSign ---> " + deviceId.toString());
        }
        return deviceId.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        String brand = "";
        try {//厂商和品牌
            if (!TextUtils.isEmpty(Build.BRAND)) {//厂商不为空
                if (!TextUtils.isEmpty(Build.MODEL)) {//型号不为空
                    brand = android.os.Build.BRAND.concat(" ").concat(Build.MODEL);
                } else {
                    brand = android.os.Build.BRAND;
                }
            } else {
                brand = "其他";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brand;
    }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static int getVersionId(Context context) {
        //获取实例
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNo = packageInfo.versionCode;
        return versionNo;
    }

    /**
     * 获取当前版本名称
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        //获取实例
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packageInfo.versionName;
        return version;
    }

    public static String getResString(int strId) {
        return ContextHelper.getApplication().getString(strId);
    }

    /**********************************以下为跳转Intent封装****************************************/

    /**
     * 重启activity
     *
     * @param activity
     */
    public static void restartActivity(Activity activity) {
//        if (Build.VERSION.SDK_INT >= 11) {
//            activity.recreate();
//        } else {
        activity.finish();
        activity.startActivity(activity.getIntent());
//        }
    }

    /**
     * 跳转intent
     *
     * @param activity
     * @param class1
     * @param pBundle
     * @param closeActivity
     */
    public static void openActicity(Activity activity, Class<?> class1, Bundle pBundle, boolean closeActivity) {
        Intent intent = new Intent(activity, class1);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }

        activity.startActivity(intent);
        if (closeActivity) {
            activity.finish();
        }

    }

    public static void openActicity(Context context, Class<?> class1, Bundle pBundle) {
        openActicity(context, class1, pBundle, 0);
    }

    public static void openActicity(Context context, Class<?> class1, Bundle pBundle, int flags) {
        Intent intent = new Intent(context, class1);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }

        if (flags > 0) {
            intent.setFlags(flags);
        }
        context.startActivity(intent);
    }

    public static void openActivityForResult(Activity activity, Class<?> class1, int code) {
        Intent intent = new Intent(activity, class1);
        activity.startActivityForResult(intent, code);
    }

    public static void openActivityForResult(Activity activity, Fragment fragment, Class<?> class1, int code) {
        Intent intent = new Intent(activity, class1);
        fragment.startActivityForResult(intent, code);
    }

    public static void goToLogin(Context context) {
        //检测手机上是否安装了微信
//        if (MyApplicaion.iwxapi.isWXAppInstalled()) {
//            CommonUtil2.openActicity(activity, LoginByWeChatActivity.class, null);
//        } else {
//            CommonUtil2.openActicity(activity, LoginWithoutWeChatActivity.class, null);
//        }
        openActicity(context, LoginActivity.class, null);
    }

    /**
     * 跳转到新的activity判断是否登录
     *
     * @param activity      现在的Activity
     * @param class1        目标Activity
     * @param pBundle
     * @param closeActivity 是否关闭当前Activity
     */
    public static void openActivityWithLogin(Activity activity, Class<?> class1,
                                             Bundle pBundle, boolean closeActivity) {
        if (SPUtil.isLogin()) {
            Intent intent = new Intent(activity, class1);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            activity.startActivity(intent);
        } else {
            goToLogin(activity);
        }

        if (closeActivity) {
            activity.finish();
        }

    }

    /**
     * 跳转到新的activity判断是否登录
     *
     * @param context 上下文
     * @param class1  目标Activity
     * @param pBundle
     */
    public static void openActivityWithLogin(Context context, Class<?> class1,
                                             Bundle pBundle) {
        if (SPUtil.isLogin()) {
            Intent intent = new Intent(context, class1);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            context.startActivity(intent);
        } else {
            goToLogin(context);
        }
    }

    /**
     * 处理逻辑判断是否登录 否则跳转登录
     *
     * @param context
     * @return
     */
    public static boolean isLoginElseGotoLogin(Context context) {
        if (SPUtil.isLogin()) return true;
        goToLogin(context);
        return false;
    }
/***************************************************************************************************/
    /**
     * 设置屏幕半透明
     *
     * @param activity
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        if (activity != null) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgAlpha;
            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 是否开启GPS开关
     *
     * @return
     */
    public static boolean hasGPSDevice() {
        LocationManager locationManager
                = (LocationManager) ContextHelper.getApplication().getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    //拼接数组
    public static byte[] ArrayConcatB(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    //拼接数组
    public static String[] ArrayConcatS(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 字符串转数组
     *
     * @param str
     * @return
     */
    public static String[] stringToArray(String str) {
        String s = str.trim();
        String[] arrays = new String[s.length()];
        for (int i = 0; i < s.length(); i++) {
            arrays[i] = s.substring(i, i + 1);
        }
        return arrays;
    }

    /**
     * 获得系统状态栏高度
     **/
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = ContextHelper.getApplication().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ContextHelper.getApplication().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断集合是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean ListCheck(List<String> list1, List<String> list2) {
        if (list1.size() == 0 && list2.size() == 0) return false;
        if (list1.size() != list2.size()) return false;
        if (list2.containsAll(list1))
            return true;
        return false;
    }

    /**
     * 判断集合是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean CheckList(List<Object> list1, List<Object> list2) {
        if (list1.size() == 0 && list2.size() == 0) return false;
        if (list1.size() != list2.size()) return false;
        if (list2.containsAll(list1))
            return true;
        return false;
    }

    /**
     * 排序
     *
     * @param list
     */
    public static void SortCollection(List<String> list) {
        Collections.sort(list, (s, t) -> {
            int s1 = Integer.valueOf(s);
            int s2 = Integer.valueOf(t);
            if (s1 < s2) return -1;
            if (s1 == s2) return 0;
            return 1;
        });
    }

    /**
     * byte[]排序
     *
     * @param list
     */
    public static void SortListByteCollection(List<byte[]> list) {
        Collections.sort(list, new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                for (int i = 0; i < o1.length; i++) {
                    if (o1[i] < o2[i]) {
                        return -1;
                    } else {
                        if (o1[i] > o2[i]) {
                            return 1;
                        } else {
                            continue;
                        }
                    }
                }
                return 0;
            }
        });
    }
    /***********************k线lib******************************************/
    public static double round2(double value) {
        return round(value, 2, 4);
    }

    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        return bd.doubleValue();
    }

    public static double round8(double value) {
        return CommonUtil.round(value, 8, BigDecimal.ROUND_HALF_UP);
    }

    public static double getMarketInfoAdapter(double value) {
        return value > 1 || value < -1 ? CommonUtil.round2(value) : round8(value);
    }

    public static BigDecimal getMarketInfoAdapterBigDecimal(double value) {
        return getBigDecimalFromDouble(value, value > 1 || value < -1 ? 2 : 8);
    }

    public static String getMarketInfoAdapterString(double value) {
        return getDoubleStringFromDouble(value, value > 1 || value < -1 ? 2 : 8);
    }
    /*********************************************************************/
    // 将科学计数法转换成一般数字 比如-1.23E-3 转换为 -0.00123
    public static BigDecimal getBigDecimalFromDouble(double value, int newScale) {
        BigDecimal bd2 = new BigDecimal(value);
        return bd2.setScale(newScale, BigDecimal.ROUND_HALF_UP);
    }

    // 将科学计数法转换成一般数字 比如-1.23E-3 转换为 -0.00123 不四舍五入 0.000001
    public static String getDoubleStringFromDouble(double value, int newScale) {
        if (newScale <= 0) return "" + value;
        BigDecimal bd2 = new BigDecimal(value);
        String str = bd2.setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString();
        String valueString = str;
        for (int i = 0; i < str.length(); i++) {
            if (valueString.endsWith("0")) {
                valueString = valueString.substring(0, valueString.length() - 1);
            } else if (valueString.endsWith(".")) {
                valueString = valueString.substring(0, valueString.length() - 1);
                break;
            } else
                break;
        }
        return valueString;
    }

/**************************************************************************************/
    /**
     * 判断手机是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                //mNetworkInfo.isAvailable();
                return true;//有网
            } else {
                return false;//没有网
            }
        }
        return false;

    }

    /**
     * 获得渠道名称
     *
     * @return
     */
    public static String getAppChannel() {
        String channel = "LendChain";
        try {
            ApplicationInfo appInfo = ContextHelper.getApplication().getPackageManager()
                    .getApplicationInfo(ContextHelper.getApplication().getPackageName(),
                            PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("Channel", channel);
        return channel;
    }

    /**
     * 复制到剪贴板
     *
     * @param str
     */
    public static void clipboardStr(String str) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) ContextHelper.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", str);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 获取剪贴板内容 粘贴
     */
    public static String clipboardPasteStr() {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) ContextHelper.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = cm.getPrimaryClip();
        //获取到内容
        ClipData.Item item = mClipData.getItemAt(0);
        return item.getText().toString();
    }

    /**
     * byte转stringList
     *
     * @return
     */
    public static List<String> bytetoList(byte[] byteData) {
        List<String> list = new ArrayList<>();
        for (byte data : byteData) {
            list.add(String.valueOf(data));
        }
        return list;
    }

    /**
     * 判断是否为11位电话
     *
     * @return
     */
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[0-3|5-9])|(18[0-9])|(16[6])|(14[5|7|9])|(17[1|3|6-8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 身份证号*****
     *
     * @param id
     * @return
     */
    public static String hideIdCard(String id) {
        String idNum = id.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1****$2");
        return idNum;
    }

    /**
     * 手机号*****
     *
     * @param phone
     * @return
     */
    public static String hidePhoneNum(String phone) {
        String phone_s = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return phone_s;
    }

    /**
     * 8-20位密码 包含大写 小写 数字 特殊符号 3种以上
     *
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd) {
        Pattern p = Pattern.compile(
                "^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,20}$");
        Matcher m = p.matcher(pwd);
        return m.find();
    }

    /**
     * 正则校验
     *
     * @return
     */
    public static boolean checkPwd(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.find();
    }

}

package com.lend.lendchain.utils;

import com.lend.lendchain.R;
import com.lend.lendchain.helper.ContextHelper;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class TimeUtils {
    public static final String YYYY_MM_dd_HH_MM_SS="yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_dd_HH_MM_SS1="yyyy.MM.dd HH:mm:ss";
    public static final String HH_MM_SS="HH:mm:ss";
    /**
     * 获得当前时间
     * @return
     */
    public static String getSystemDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /*时间戳转换成字符窜毫秒*/
    public static String getDateToString(long time) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /*时间戳转换成字符窜毫秒*/
    public static String getDateToString2(long time) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
    /*自定义格式时间戳毫秒转换成字符窜*/
    public static String getDateToStringMs(long time, String format) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat(format);
        return sf.format(d);
    }
    /*自定义格式时间戳秒转换成字符窜*/
//    yyyy-MM-dd HH:mm:ss
    public static String getDateToStringS(long time, String format) {
        SimpleDateFormat sf = null;
        Date d = new Date(time*1000);
        sf = new SimpleDateFormat(format);
        return sf.format(d);
    }

    /**
     * 日期转星期
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获得几天前的时间
     * @return
     */
    public static String getRageDate(int days, String format){
        Calendar c = Calendar.getInstance(); // 当时的日期和时间
        int day; // 需要更改的天数
        day = c.get(Calendar.DAY_OF_MONTH) - days;
        c.set(Calendar.DAY_OF_MONTH, day);
        return getDateToStringMs(c.getTimeInMillis(),format);
    }

    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static String getDistanceTime(long  time1,long time2 ) {
//        SimpleDateFormat df = new SimpleDateFormat("dd天 HH:mm:ss");
//        Date date = null;
//        Date date1=null;
//        try {
//            date = df.parse(String.valueOf(time1));
//            date1= df.parse(String.valueOf(time2));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        long defferenttime = date.getTime() - date1.getTime();
//        long days = defferenttime / (1000 * 60 * 60 * 24);
//        long hours = (defferenttime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//        long minute = (defferenttime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
//        long seconds = defferenttime % 60000;
//        long second = Math.round((float) seconds / 1000);

        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        if(time1<time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        String dayStr= ContextHelper.getApplication().getString(R.string.day_tian);
        String colonStr= ContextHelper.getApplication().getString(R.string.invest_colon);
        String laterStr= ContextHelper.getApplication().getString(R.string.invest_later);
        //5天 10:10:20 之后
        return day+dayStr+" "+hour+colonStr+min+colonStr+sec+" "+laterStr;//8天 2时40分5秒
    }

}

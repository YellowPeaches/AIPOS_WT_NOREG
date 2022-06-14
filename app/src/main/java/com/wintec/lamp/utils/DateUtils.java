package com.wintec.lamp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static String getDate(String format) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static String getDateAddDays(String format, int days) {
        if (format == null || "".equals(format)) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, days);
        date = calendar.getTime();
        return dateFormat.format(date);
    }

    public static String getTime(String timeString) {
        String timeStamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date d;
        try {
            d = sdf.parse(timeString);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

//    public static String getBarTime() {
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM:SS");
//        Date date = new Date(System.currentTimeMillis());
//        return dateFormat.format(date);
//    }
//
//    public static String getDate() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
//        Date date = new Date(System.currentTimeMillis());
//        return dateFormat.format(date);
//    }
}

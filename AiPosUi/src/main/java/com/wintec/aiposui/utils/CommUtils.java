package com.wintec.aiposui.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

public class CommUtils {
    public static DecimalFormat decimalFormat;

    /**
     * float转String,并指定保留小数
     * @param value
     * @param num 2保留两位小数,3保留三位小数
     * @return
     */
    public static String Float2String(float value, int num){
//        switch (num){
//            case 2:
//                decimalFormat = new DecimalFormat("#0.00");
//                break;
//            case 3:
//                decimalFormat = new DecimalFormat("#0.000");
//                break;
//            default:
//                return "";
//        }
//        return decimalFormat.format(value);
        BigDecimal bigDecimal = new BigDecimal(value+"");
        switch (num){
            case 2:
                decimalFormat = new DecimalFormat("#0.00");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                break;
            case 3:
                decimalFormat = new DecimalFormat("#0.000");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                break;
            default:
                return "";
        }
        return decimalFormat.format(bigDecimal);
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
    public static String getIP(Context context) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

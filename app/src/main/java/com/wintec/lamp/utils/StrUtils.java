package com.wintec.lamp.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.google.gson.Gson;

public class StrUtils {

    //base64字符串转byte[]
//    public static byte[] base64String2ByteFun(String base64Str){
//        return Base64.(base64Str);
//    }
    //byte[]转base64
    public static String byte2Base64StringFun(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String getJsonStringByEntity(Object o) {
        String strJson = "";
        Gson gson = new Gson();
        strJson = gson.toJson(o);
        return strJson;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取到版本号";
        }
    }
}

package com.wintec.lamp.utils;

import android.content.Context;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:16
 */
public class ContextUtils {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getApp() {
        return mContext;
    }
}

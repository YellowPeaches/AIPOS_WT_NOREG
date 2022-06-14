package com.wintec.lamp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SPUtils {
    private static final String SP_NAME = "wintec_pay";//这个就是一个名字，随便起
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private SPUtils(Context context) {
        this.context = context;
        sp = this.context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    private static SPUtils mSpUtils;//

    public static SPUtils getInstance(Context context) {

        if (mSpUtils == null) {
            synchronized (SPUtils.class) {
                if (mSpUtils == null) {
                    mSpUtils = new SPUtils(context);
                    return mSpUtils;
                }
            }
        }
        return mSpUtils;
    }

    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, Boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        if (key == null) {
            return;
        }
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public Long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }


    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }
}

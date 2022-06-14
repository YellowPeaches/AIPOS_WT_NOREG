package com.wintec.lamp.utils.log;

import android.content.Context;
import android.os.Environment;


import com.common.log.DatePatternType;
import com.common.log.LogUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logging {

    public static final String TAG = "AIPOS";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String ROOT_PATH = Environment.getExternalStoragePublicDirectory("DIRECTORY_DOCUMENTS").getPath();

    public static Lock lock = new ReentrantLock(true);

    public Logging(Context context) {
        File file = new File(ROOT_PATH + "//aipos_log//" + getLogFileName());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        LogUtils.getInstance(context)
                .setUseLogCatAppender(true)
                .setLogCatPattern("%m%n")
                .setFileName(ROOT_PATH + "//aipos_log//" + getLogFileName())
                .setUseFileAppender(false)
                .setMaxFileSize(524288L)
                .setMaxBackupSize(5)
                .setUseDailyFileAppender(true)
                .setKeepDays(7)
                .setDatePatternType(DatePatternType.TOP_OF_DAY)
                .setFilePattern("%d{yyy-MM-dd HH:mm:ss} %p %t %l %m%n")
                .init();
    }

    public void i(String message) {
        LogUtils.i(TAG, message);
    }

    public void e(String message) {
        LogUtils.e(TAG, message);
    }

    public void w(String message) {
        LogUtils.w(TAG, message);
    }

    public void d(String message) {
        LogUtils.d(TAG, message);
    }

    /**
     * 获取LOG文件名
     *
     * @return
     */
    public String getLogFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date) + ".txt";
    }

    public String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

}

package com.wintec.lamp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.wintec.lamp.base.Const;
import com.wintec.lamp.mvp.ModelImpl;
import com.wintec.lamp.utils.log.Logging;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
    protected ModelImpl modelImpl;

    public static CrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 初始化
     * Unknown column 'user_id' in 'field list'
     *
     * @param context
     */
    public void init(Context context) {
        //得到系统的应用异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前应用异常处理器改为默认的
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当系统中有未被捕获的异常，系统将会自动调用 uncaughtException 方法
     *
     * @param thread 为出现未捕获异常的线程
     * @param ex     为未捕获的异常 ，可以通过e 拿到异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringBuffer logInfo = new StringBuffer();
        logInfo.append("\n");
        Log.e("程序出现异常了", "Thread = " + thread.getName() + "\nThrowable = " + ex.getMessage());
        for (Map.Entry<String, String> entry : obtainSimpleInfo(mContext).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logInfo.append(key).append(" = ").append(value).append("\n");
        }
        logInfo.append(obtainExceptionInfo(ex));
        String errorMessage = new Date() + logInfo.toString();
        //保存日志到本地
        saveErrorLogLocal(errorMessage, false);

        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }

    private void writeStringToFile(final String errorMessage, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outputStream = null;
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(errorMessage.getBytes());
                    outputStream = new FileOutputStream(file);
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                    }
                    outputStream.flush();
                    Log.e("程序出异常了", "写入本地文件成功：" + file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 保存日至到本地
     *
     * @param errorMessage
     * @param flag
     */
    private void saveErrorLogLocal(String errorMessage, boolean flag) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }
        List<MultipartBody.Part> partList = new ArrayList<>();
        String mediaType = "text/plain";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        writeFile(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt", errorMessage);
        //error标记
        Const.setSettingValue("ERROR_LOG_FLAG", "1");
        //error路径
        Const.setSettingValue("ERROR_LOG_PATH", Logging.ROOT_PATH + "//aipos_log//" + format + ".txt");
    }

    /**
     * 将字符串追加到文件已有内容后面
     *
     * @param fileFullPath 文件完整地址：D:/test.txt
     * @param content      需要写入的
     */
    public void writeFile(String fileFullPath, String content) {
        FileOutputStream fos = null;
        try {
            //true不覆盖已有内容
            fos = new FileOutputStream(fileFullPath, true);
            //写入
            fos.write(content.getBytes());
            // 写入一个换行
            fos.write("\r\n".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);
        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);
        map.put("SN ", Const.SN);
        return map;
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */

    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }
}

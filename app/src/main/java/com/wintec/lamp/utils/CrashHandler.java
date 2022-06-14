package com.wintec.lamp.utils;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.wintec.lamp.api.AppRetrofitApi;
import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.network.RetrofitClient;
import com.wintec.lamp.network.RetrofitSubscriber;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.utils.log.Logging;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

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
////        LogUtils.e("程序崩溃异常："+ex.getMessage());
//        StringBuffer b=new StringBuffer();
//        //获取错误信息
//        b.append(ex.getMessage());
//        b.append("\n");
//        b.append(ex.toString());
//        //拿到的错误信息
//
//        for (StackTraceElement te:ex.getStackTrace()){
//            b.append("Class:"+te.getClassName()+";LineNumber:"+te.getLineNumber()+"\n");
//        }
////        LogUtils.e(b.toString());
//        uploadExceptionToServer(b.toString(),false);
//        ex.printStackTrace();
        Log.e("程序出现异常了", "Thread = " + thread.getName() + "\nThrowable = " + ex.getMessage());
        String stackTraceInfo = getStackTraceInfo(ex);
        Log.e("stackTraceInfo", stackTraceInfo);
        saveThrowableMessage(stackTraceInfo);

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

    private void saveThrowableMessage(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        File file = new File(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt");

        //writeStringToFile(errorMessage, file);
        errorMessage = new Date() + errorMessage;
        uploadExceptionToServer(errorMessage, false);

    }


    private void uploadExceptionToServer(String b, boolean flag) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        String mediaType = "image/jpeg";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        writeFile(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt", b);
        File file = new File(Logging.ROOT_PATH + "//aipos_log//" + format + ".txt");
        if (file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("imgFile", file.getName(), requestFile);
            partList.add(part);
        }
        if (!flag) {
            return;
        }
        AppRetrofitApi retrofitApi = RetrofitClient.getInstance().createService(AppRetrofitApi.class);
        retrofitApi.upLog(partList.get(0), file.getName(), Const.SN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitSubscriber<HttpResponse<String>>(null) {
                    @Override
                    protected void onSuccess(HttpResponse<String> response) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
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
}

package com.wintec.lamp.base;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.flattener.Flattener2;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;
import com.wintec.lamp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/5
 * Time: 下午3:23
 */
public class BaseActivityNew extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    public static final String PARAMS_BUNDLE = "PARAMS_BUNDLE";
    public static final String PARAMS_RC = "PARAMS_RC";
    protected Bundle mBundleExtra;
    protected int mRequestCode;
    private Dialog mDialog;
    protected Activity mContext;

    private static String ROOT_PATH = Environment.getExternalStoragePublicDirectory("DIRECTORY_DOCUMENTS").getPath() + "//aipos_log//";
    private static int logLevel = LogLevel.INFO;
    /**
     * 是否需要把dialog圆圈颜色设置为白色(默认为false)
     */
    protected boolean isNeedDialogWhite = false;

    protected int statusBar = 3;
    public static final int TRANSPARENT_BLACK_STATUS = 1; //透明状态栏，黑色字体
    public static final int TRANSPARENT_WHITE_STATUS = 2; //透明状态栏,白色字体
    public static final int NOTRANSPARENT_WHITE_STATUS = 3;//白底黑字

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        mContext = this;
        //权限
        requestPermissions();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.deepgreen1));
        }
//        ImmersionBar immersionBar = ImmersionBar.with(this);
//        switch (statusBar) {
//            case 2:
//                immersionBar.fitsSystemWindows(false).navigationBarEnable(false).statusBarDarkFont(false).init();
//                break;
//            case 3:
//                immersionBar.statusBarColor(R.color.color_FFFFFF);
//                immersionBar.fitsSystemWindows(true).navigationBarEnable(false).statusBarDarkFont(true).init();
//                break;
//            default:
//                immersionBar.fitsSystemWindows(false).navigationBarEnable(false).statusBarDarkFont(true).init();
//        }
//        StackManager.addActivity(this);
        initBundle();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        // 初始化日志
        initLogging();
        com.elvishew.xlog.XLog.i("xlog初始化日志完成");
    }

    /**
     * 设置状态栏
     *
     * @param statusBars 状态值
     */
    public void setStatusBar(int statusBars) {
        statusBar = statusBars;
    }

    private void initBundle() {
        mBundleExtra = getIntent().getBundleExtra(PARAMS_BUNDLE);
        if (mBundleExtra != null) {
            mRequestCode = mBundleExtra.getInt(PARAMS_RC);
        }
    }

    /**
     * 跳转
     *
     * @param clz
     */
    protected void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    protected void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(BaseActivityNew.this, clz);
        if (bundle != null) {
            intent.putExtra(PARAMS_BUNDLE, bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(Class activityClass, int requestCode) {
        startActivity(activityClass, null, requestCode);
    }

    protected void startActivity(Class activityClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(PARAMS_RC, requestCode);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }

    /**
     * 拨打电话
     *
     * @param phoneNumber
     */
    protected void startCall(@NonNull String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
    }

    /**
     * 显示dialog加载框
     */
//    protected void showDialog() {
//        if (null == mDialog) {
//            mDialog = LoadingUtils.createLoadingDialog(this, isNeedDialogWhite);
//        }
//        if (!mDialog.isShowing()) {
//            mDialog.show();
//        }
//    }

    /**
     * 关闭dialog加载框
     */
    protected void closeDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 请求权限
     */
    public void requestPermissions() {

        if (Build.VERSION.SDK_INT >= 30) {//30
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                //跳转到设置界面引导用户打开
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 3);
            }
        }
        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mContext,
                        new String[]{android.Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.LOCATION_HARDWARE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_SETTINGS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.RECORD_AUDIO,
//                                Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
//                jumpToMainActivity();
            }
        }

    }

    public static void initLogging() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(logLevel)
                .tag("XLog")
                .build();
        AndroidPrinter androidPrinter = new AndroidPrinter(true);         // Printer that print the log using android.util.Log 使用android.util.Log打印日志的打印机
        FilePrinter filePrinter = new FilePrinter                      // Printer that print(save) the log to file 打印(保存)日志到文件的打印机
                .Builder(ROOT_PATH)// Specify the directory path of log file(s) 指定日志文件的目录路径
                .fileNameGenerator(new MyFileNameGenerator()) //自定义文件名称 默认值:ChangelessFileNameGenerator(“日志”)
                .backupStrategy(new FileSizeBackupStrategy(6 * 1024 * 1024)) //单个日志文件的大小默认:FileSizeBackupStrategy(1024 * 1024)
                .cleanStrategy(new FileLastModifiedCleanStrategy(30L * 24L * 60L * 60L * 1000L))  //日志文件存活时间，单位毫秒
                .flattener(new MyFlattener()) //自定义flattener，控制打印格式
                .build();

        com.elvishew.xlog.XLog.init(config, androidPrinter, filePrinter);
        com.elvishew.xlog.XLog.i("XLog初始化完成");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

class MyFileNameGenerator implements FileNameGenerator {

    @Override
    public boolean isFileNameChangeable() {
        return false;
    }

    @Override
    public String generateFileName(int logLevel, long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date) + ".txt";
    }
}

class MyFlattener implements Flattener, Flattener2 {

    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {
        return flatten(System.currentTimeMillis(), logLevel, tag, message);
    }

    @Override
    public CharSequence flatten(long timeMillis, int logLevel, String tag, String message) {
        return (getCurrDDate(timeMillis)
                + '|' + LogLevel.getLevelName(logLevel)
                + '|' + tag
                + '|' + message);

    }

    private String getCurrDDate(long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date(timeMillis);
        return dateFormat.format(date);
    }
}
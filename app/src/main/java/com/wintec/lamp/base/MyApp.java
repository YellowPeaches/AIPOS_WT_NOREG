package com.wintec.lamp.base;

import android.database.sqlite.SQLiteDatabase;
import android.hardware.camera2.CameraCharacteristics;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.wintec.detection.WtAISDK;
import com.wintec.detection.utils.LogUtils;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.dao.DaoMaster;
import com.wintec.lamp.dao.DaoSession;
import com.wintec.lamp.dao.helper.GreenDaoUpgradeHelper;
import com.wintec.lamp.network.NetWorkManager;
import com.wintec.lamp.utils.ContextUtils;
import com.wintec.lamp.utils.CrashHandler;

public class MyApp extends BaseApp {

    private static DaoSession daoSession;
    private static SQLiteDatabase db;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
//        ExceptionCrashHandler.getInstance().init(this);
        ContextUtils.init(this);
//        LogcatHelper.getInstance(this).start();
        setupDatabase();
        //程序奔溃异常
        CrashHandler.getInstance().init(this);

        refWatcher = setupLeakCanary();
        float minScore = 0.65F;   //置信度
        int showNum = 5;          //回传结果个数
        if (StringUtils.isNotBlank(Const.getSettingValue(Const.DETECT_THRESHOLD))) {
            minScore = Float.parseFloat(Const.getSettingValue(Const.DETECT_THRESHOLD));
        }
        if (StringUtils.isNotBlank(Const.getSettingValue(Const.KEY_GOODS_COUNT))) {
            showNum = Integer.parseInt(Const.getSettingValue(Const.KEY_GOODS_COUNT));
        }
//        int code = WtAISDK.api_InitSDK(this, false);
        int code = WtAISDK.api_InitSDK(this, false, 0, minScore, showNum);
//        int code = WtAISDK.api_InitSDK(this, false, 1, minScore, showNum,true);
        LogUtils.d("code:" + code);

        // 初始化网络框架
        NetWorkManager.getInstance().init();
        //字体
//        TypefaceUtil.replaceSystemDefaultFont(this,"fonts/msyh.ttf");
//        xcrash.XCrash.init(this);
//        LogUtils.i("程序启动"+ WintecDevInfo.getDevSn());

        //根据摄像头选择 CameraCharacteristics.LENS_FACING_FRONT
        WtAISDK.api_setCameraId(CameraCharacteristics.LENS_FACING_FRONT);
    }


    public static RefWatcher getRefWatcher() {
        return ((MyApp) getContext()).refWatcher;
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db
        GreenDaoUpgradeHelper helper = new GreenDaoUpgradeHelper(this, "wintec_aipos.db", null);
        //获取可写数据库
        db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();

    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    public static SQLiteDatabase getSQLiteDatabase() {
        return db;
    }
}
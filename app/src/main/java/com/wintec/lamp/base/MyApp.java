package com.wintec.lamp.base;

import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.wintec.aiposui.view.AiPosAllView;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.detection.WtAISDK;
import com.wintec.lamp.R;
import com.wintec.lamp.dao.DaoMaster;
import com.wintec.lamp.dao.DaoSession;
import com.wintec.lamp.dao.helper.GreenDaoUpgradeHelper;
import com.wintec.lamp.network.NetWorkManager;
import com.wintec.lamp.utils.ContextUtils;
import com.wintec.lamp.utils.CrashHandler;
import com.wintec.lamp.utils.wintecLable.Handler;
import com.wintec.lamp.utils.wintecLable.SocketServer;

import java.util.List;

import butterknife.BindView;

public class MyApp extends BaseApp {

    private static DaoSession daoSession;
    private static SQLiteDatabase db;
    private RefWatcher refWatcher;
    private AiTipDialog aiTipDialog;

    @BindView(R.id.aipos)
    AiPosAllView aiPosAllView;

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
        //置信度
        float minScore = Float.parseFloat(Const.getSettingValueWithDef(Const.DETECT_THRESHOLD,"0.65"));
        //回传结果个数
        int showNum = Integer.parseInt(Const.getSettingValueWithDef(Const.KEY_GOODS_COUNT,"5"));
//        int code = WtAISDK.api_InitSDK(this, false);
        int code = WtAISDK.api_InitSDK(this, false, 0, minScore, showNum);
        WtAISDK.api_setParam(Const.SN, "zkyt", "210629001", 2);
        // 初始化网络框架
        NetWorkManager.getInstance().init();
//        检查摄像头
        boolean cameraEnable = checkCameraEnable();
        if (!cameraEnable) {
            Toast.makeText(this, "相机未连接", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "相机未连接", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "相机未连接", Toast.LENGTH_LONG).show();
        }

        //根据摄像头选择 CameraCharacteristics.LENS_FACING_FRONT
        WtAISDK.api_setCameraId(CameraCharacteristics.LENS_FACING_FRONT);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketServer.getInstance(new Handler() {
                    @Override
                    public void returnMe() {

                    }
                }, 9000).init();
            }
        }).start();

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

    public static boolean checkCameraEnable() {
        boolean result;
        Camera camera = null;
        try {
            camera = Camera.open();
            if (camera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                boolean connected = false;
                for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                    try {
                        camera = Camera.open(camIdx);
                        connected = true;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    if (connected) {
                        break;
                    }
                }
            }
            List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            result = supportedPreviewSizes != null;
            /* Finally we are ready to start the preview */

            camera.startPreview();
        } catch (Exception e) {
            result = false;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
        return result;
    }
}
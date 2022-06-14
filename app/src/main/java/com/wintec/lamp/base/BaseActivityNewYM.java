package com.wintec.lamp.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wintec.detection.aicore.support.AiSupportActivity;
import com.wintec.lamp.R;
import com.wintec.detection.aicore.support.AiSupportActivity;
import com.wintec.lamp.presenter.WelcomePresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/5
 * Time: 下午3:23
 */
public class BaseActivityNewYM extends AiSupportActivity {

    protected final String TAG = this.getClass().getSimpleName();
    public static final String PARAMS_BUNDLE = "PARAMS_BUNDLE";
    public static final String PARAMS_RC = "PARAMS_RC";
    protected Bundle mBundleExtra;
    protected int mRequestCode;
    private Dialog mDialog;

    protected Activity mContext;
    /**
     * 是否需要把dialog圆圈颜色设置为白色(默认为false)
     */
    protected boolean isNeedDialogWhite = false;

    protected int statusBar = 3;
    public static final int TRANSPARENT_BLACK_STATUS = 1; //透明状态栏，黑色字体
    public static final int TRANSPARENT_WHITE_STATUS = 2; //透明状态栏,白色字体
    public static final int NOTRANSPARENT_WHITE_STATUS = 3;//白底黑字

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setBackgroundDrawable(null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // 设置全屏模式
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.deepgreen1));
        }

//        new WelcomePresenter().upPLUDto();
        initBundle();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
        Intent intent = new Intent(BaseActivityNewYM.this, clz);
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
        intent.putExtra(PARAMS_BUNDLE, bundle);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

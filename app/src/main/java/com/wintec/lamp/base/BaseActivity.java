package com.wintec.lamp.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wintec.lamp.R;

import java.io.Serializable;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // 设置全屏模式
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.deepgreen1));
        }
        mContext = this;
        setContentView(getView());
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    public abstract int getView();

    protected void init(Bundle savedInstanceState) {
    }

    public void initView() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //创建对象制定日期格式
//        String date = format.format(new Date());
    }

    protected void initData() {
    }

    public void initListener() {
    }


    public void MyToast(String mes) {
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
    }

    public void jumpToActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void jumpToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        jumpToActivity(intent);
    }

    public void jumpToActivity(Class activity, String key, String extra) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(key, extra);
        jumpToActivity(intent);
    }

    public void jumpToActivity(Class activity, String key, Serializable value) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(key, value);
        jumpToActivity(intent);
    }

    public void jumpToActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void jumpToActivityForResult(Class activity, int requestCode) {
        Intent intent = new Intent(this, activity);
        jumpToActivityForResult(intent, requestCode);
    }

    public void jumpToActivityForResult(Class activity, int requestCode, String key, String extra) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(key, extra);
        jumpToActivityForResult(intent, requestCode);
    }


    public void startToService(Intent intent) {
        startService(intent);
    }

    protected void LockOrientation() {
        // Activity启动后就锁定为启动时的方向
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

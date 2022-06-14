package com.wintec.lamp.utils.scale;

import android.util.Log;

import cn.wintec.wtandroidjar.SCL;

/**
 * @author 赵冲
 * @description:
 * @date :2021/6/3 10:10
 */
public abstract class ScalesObject {

    protected final static String SCALES_DEVICES = "/dev/ttySAC1";
    protected final static String SCALES_DEVICES1 = "/dev/ttyS1";
    protected ScalesObject.ScalesCallback callback;
    protected Thread thread;
    protected Runnable runnable;
    protected boolean isReadScales = true; // 标识符, 控制是否读秤示数

    public interface ScalesCallback {
        void getData(float net, float preNet, float tare, String status);
    }

    public ScalesObject(ScalesObject.ScalesCallback callback) {
        this.callback = callback;
    }

    protected float preNet = 0.000f;

    /**
     * WGT:1 0.182P 0.000
     * status bit0 表示稳定标志，值1为秤已经稳定 &1
     * bit1 表示零点标志，值1为秤在零点 &2
     * bit2 表示去皮标志，值1位秤已去皮 &4
     */
    protected abstract void readData();

    /**
     * 秤归零
     */
    public abstract void sendZero();

    /**
     * 设置皮重
     */
    public abstract void sendTare();

    /**
     * 设置固定皮重
     */
    public abstract void sendYtare(float tare);

    public abstract void close();


}

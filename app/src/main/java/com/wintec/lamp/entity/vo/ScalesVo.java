package com.wintec.lamp.entity.vo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/23 13:38
 */
public class ScalesVo {

    private final static int SCALES_CLEAR = 10;    // 清空秤盘
    private final static int SCALES_SHOW_WHITE = 11;    // 展示秤示数
    private final static int SCALES_SHOW_RED = 12;    // 展示秤示数
    private final static int SCALES_DETECT = 13;    // 通知识别

    private float net = 0;                // 秤重量
    private String scaleStatus = "US";    // 秤状态,重量稳定发送 ST，重量不稳定发送 US，超重发送 OL

    private AtomicBoolean isWritingCom = new AtomicBoolean(false);    // 标识符,控制是否正在写串口
    private AtomicBoolean isCanDectect = new AtomicBoolean(true);     // 标识符,控制能否识别
    private AtomicBoolean isCanClear = new AtomicBoolean(true);     // 标识符，控制是否可以清空秤盘
    private AtomicBoolean isCanReDetect = new AtomicBoolean(false);
}

package com.wintec.lamp.entity;

import android.view.View;

/**
 * @描述：
 * @文件名: AiPos.OperatingBtn
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/18 16:57
 */
public class OperatingBtn {
    private String title;
    private View.OnClickListener listener;
    private View.OnLongClickListener longClickListener;

    public OperatingBtn(String title, View.OnClickListener listener) {
        this.title = title;
        this.listener = listener;
    }

    public OperatingBtn(String title, View.OnClickListener listener, View.OnLongClickListener longClickListener) {
        this.title = title;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public View.OnLongClickListener getLongClickListener() {
        return longClickListener;
    }

    public void setLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}

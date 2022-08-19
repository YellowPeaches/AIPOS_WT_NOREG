package com.wintec.aiposui.view.dialog;

import android.content.Context;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**
 * @描述：
 * @文件名: AiPos.AiTipDialog
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/21 9:41
 */
public class AiTipDialog {

    private QMUITipDialog tipDialog;

    public void showSuccess(String tipWord, View parentView) {
        tipDialog = new QMUITipDialog.Builder(parentView.getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(tipWord)
                .create();
        tipDialog.setCancelable(true);
        tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        parentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 600);
    }

    public void showFail(String tipWord, View parentView) {
        tipDialog = new QMUITipDialog.Builder(parentView.getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(tipWord)
                .create();
        tipDialog.setCancelable(true);
        tipDialog.setCanceledOnTouchOutside(true);
        tipDialog.show();
        parentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 600);
    }


    /**
     * loading提示框
     */
    public QMUITipDialog showLoading(String tipWord, Context context) {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tipWord)
                .create();
        tipDialog.show();
        return tipDialog;
    }

    public QMUITipDialog dataLoading(String tipWord, View parentView, Integer delayMillis) {
        tipDialog = new QMUITipDialog.Builder(parentView.getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tipWord)
                .create();
        tipDialog.show();
        parentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, delayMillis);
        return tipDialog;
    }

    public void dismiss() {
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
    }

}

package com.wintec.lamp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.wintec.lamp.R;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/22 15:05
 */
public class ResultDialog {
    static Dialog loading = null;

    public static Dialog creatAlertDialog(Context context, View view) {
        Dialog loading = new Dialog(context, R.style.commonDialog);
        loading.setCancelable(true);
        loading.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (LinearLayout.LayoutParams.MATCH_PARENT)));
        return loading;
    }
}

package com.wintec.lamp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.wintec.lamp.R;

public class ToastUtils {
    private static Toast lastToast = null;

    public static void showToast(String str) {
        Toast currentToast = Toast.makeText(ContextUtils.getApp(), str, Toast.LENGTH_SHORT);
        View toastLayout = ((LayoutInflater) ContextUtils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        AppCompatTextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        toastTextView.setText(str);
        currentToast.setView(toastLayout);
        if (lastToast != null) {
            lastToast.cancel();
        }
        lastToast = currentToast;
        lastToast.show();
    }

    static void cancel() {
        if (lastToast != null) {
            lastToast.cancel();
            lastToast = null;
        }
    }
}

package com.wintec.lamp.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wintec.lamp.WelcomeNoReg;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())) {
            Intent intentMainActivity = new Intent(context, WelcomeNoReg.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
        }
    }
}

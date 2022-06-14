package com.wintec.aiposui.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {
    public static DecimalFormat decimalFormat;

    /**
     * float转String,并指定保留小数
     * @param value
     * @param num 2保留两位小数,3保留三位小数
     * @return
     */
    public static String Float2String(float value, int num){
//        switch (num){
//            case 2:
//                decimalFormat = new DecimalFormat("#0.00");
//                break;
//            case 3:
//                decimalFormat = new DecimalFormat("#0.000");
//                break;
//            default:
//                return "";
//        }
//        return decimalFormat.format(value);
        BigDecimal bigDecimal = new BigDecimal(value+"");
        switch (num){
            case 2:
                decimalFormat = new DecimalFormat("#0.00");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                break;
            case 3:
                decimalFormat = new DecimalFormat("#0.000");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                break;
            default:
                return "";
        }
        return decimalFormat.format(bigDecimal);
    }

}

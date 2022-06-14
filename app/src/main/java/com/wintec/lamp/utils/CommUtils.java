package com.wintec.lamp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wintec.lamp.base.Const;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {
    public static DecimalFormat decimalFormat;

    /**
     * float转String,并指定保留小数
     *
     * @param value
     * @param num   2保留两位小数,3保留三位小数
     * @return
     */
    public static String Float2String(float value, int num) {
        BigDecimal bigDecimal = new BigDecimal(value + "");
        switch (num) {
            case 2:
                decimalFormat = new DecimalFormat("#0.00");
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
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

    public static String priceToString(float value) {
        Log.i("test", "价格 " + value);
        BigDecimal bigDecimal = new BigDecimal(value + "");
        boolean flag = false;
        switch (Const.getSettingValue(Const.TOTAL_PRICE_MODE)) {
            case "不圆整(18.16)":
                decimalFormat = new DecimalFormat("#0.00");
                flag = true;
                break;
            case "四舍五入(18.2)":
                decimalFormat = new DecimalFormat("#0.0");
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                break;
            case "强舍(18.1)":
                decimalFormat = new DecimalFormat("#0.0");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                break;
            case "强入(18.2)":
                decimalFormat = new DecimalFormat("#0.0");
                decimalFormat.setRoundingMode(RoundingMode.UP);
                break;
            case "四舍五入(18.22)":
                decimalFormat = new DecimalFormat("#0.00");
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
//                bigDecimal = new BigDecimal(value + "");
                flag = true;
                break;
            default:
                decimalFormat = new DecimalFormat("#0.00");
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                flag = true;
                break;
        }
        String price = decimalFormat.format(bigDecimal);
        Log.i("test", "格式后价格 " + price);
        if (!flag) {
            price = price + "0";
        }
        return price;
    }

    public static String weightToString(float net) {
        String point = Const.getSettingValue(Const.WEIGHT_POINT);
        switch (point) {
            case "2":
                decimalFormat = new DecimalFormat("#0.00");
                break;
            case "3":
                decimalFormat = new DecimalFormat("#0.000");
                break;
            default:
                decimalFormat = new DecimalFormat("#0.000");
                break;
        }
        String weight = decimalFormat.format(net);
        return weight;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric2(String str) {
        if (str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isCoordonate(String str) {
        if (str.equals("")) {
            return true;
        }
        if (!str.contains(",")) {
            return false;
        }
        String s = str.replaceAll(",", "");
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(s);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为int
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void showMessage(Context context, String text) {
        Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        LinearLayout linearLayout = (LinearLayout) t.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(35);
        t.show();
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static String toStandard(String number) {
        if (number == null || number.equals("")) {
            return "0.000";
        }
        if (!number.contains(".")) {
            return number + ".000";
        }
        String result = "0.000";
        try {
            String d = number.split("\\.")[1];
            switch (d.length()) {
                case 1:
                    result = number + "00";
                    break;
                case 2:
                    result = number + "0";
                    break;
                default:
                    result = number;
                    break;
            }
        } catch (Exception e) {
            Log.i("test", e.toString());
            result = "0.000";
        }
        return result;
    }

    @Deprecated
    public static String toOldPLU(String number) {

        String use = "";
        switch (number.length()) {
            case 1:
                use = "0000" + number;
                break;
            case 2:
                use = "000" + number;
                break;
            case 3:
                use = "00" + number;
                break;
            case 4:
                use = "0" + number;
                break;
            case 5:
                use = "" + number;
                break;
            default:
                use = number;
                break;


        }
        return use;
    }

    public static String deleteZero(String code) {
        int count = 0;
        for (int i = 0; i < code.length(); i++) {
            if ("0".equals(code.charAt(i))) {
                count = i;
            } else {
                break;
            }
        }
        return code.substring(count);
    }
}
